package io.github.openflocon.domain.database.usecase

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.database.models.DatabaseColumnAffinity
import io.github.openflocon.domain.database.models.DatabaseExecuteSqlResponseDomainModel
import io.github.openflocon.domain.database.models.DatabaseRowEditTargetDomainModel
import io.github.openflocon.domain.database.models.DatabaseTableDomainModel
import io.github.openflocon.domain.database.models.DeviceDataBaseId
import io.github.openflocon.domain.database.repository.DatabaseRepository
import io.github.openflocon.domain.database.utils.parseSingleTableSelect
import io.github.openflocon.domain.database.utils.quoteSqlIdentifier
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel

/**
 * How the rows returned by a query can be updated, `null` when they can't: the query must read one table,
 * and its result must hold the whole primary key of that table.
 *
 * A failure when the table could not be read, so that it can be asked again.
 */
class GetRowEditTargetUseCase(
    private val databaseRepository: DatabaseRepository,
) {
    suspend operator fun invoke(
        /** The one the rows were read from. */
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: DeviceDataBaseId,
        query: String,
        resultColumns: List<String>,
    ): Either<Throwable, DatabaseRowEditTargetDomainModel?> {
        val tableName = parseSingleTableSelect(query) ?: return Success(null)
        val response = databaseRepository.executeQuery(
            deviceIdAndPackageName = deviceIdAndPackageName,
            databaseId = databaseId,
            query = "PRAGMA table_info(${quoteSqlIdentifier(tableName)})",
        ).fold(
            doOnFailure = { return Failure(it) },
            doOnSuccess = { it },
        )
        if (response !is DatabaseExecuteSqlResponseDomainModel.Select) {
            return Failure(Throwable("the columns of $tableName could not be read"))
        }

        return Success(
            toRowEditColumns(tableColumns = extractTableColumns(response), resultColumns = resultColumns)?.let {
                DatabaseRowEditTargetDomainModel(
                    deviceId = deviceIdAndPackageName.deviceId,
                    packageName = deviceIdAndPackageName.packageName,
                    databaseId = databaseId,
                    tableName = tableName,
                    columns = it,
                )
            },
        )
    }
}

/** The columns of the result, `null` when its rows can't be updated. */
internal fun toRowEditColumns(
    tableColumns: List<DatabaseTableDomainModel.Column>,
    resultColumns: List<String>,
): List<DatabaseRowEditTargetDomainModel.Column>? {
    val columns = resultColumns.map { name ->
        tableColumns.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: return null
    }
    if (columns.distinct().size != columns.size) return null

    val primaryKey = tableColumns.filter { it.primaryKey }
    if (primaryKey.isEmpty() || !columns.containsAll(primaryKey)) return null
    if (primaryKey.any { !it.affinity().isComparable }) return null

    return columns.map {
        DatabaseRowEditTargetDomainModel.Column(
            name = it.name,
            isPrimaryKey = it.primaryKey,
            affinity = it.affinity(),
        )
    }
}

private fun DatabaseTableDomainModel.Column.affinity(): DatabaseColumnAffinity {
    val declared = type.uppercase()
    return when {
        "INT" in declared -> DatabaseColumnAffinity.Integer
        listOf("CHAR", "CLOB", "TEXT").any { it in declared } -> DatabaseColumnAffinity.Text
        declared.isBlank() || "BLOB" in declared -> DatabaseColumnAffinity.Blob
        listOf("REAL", "FLOA", "DOUB").any { it in declared } -> DatabaseColumnAffinity.Real
        else -> DatabaseColumnAffinity.Numeric
    }
}

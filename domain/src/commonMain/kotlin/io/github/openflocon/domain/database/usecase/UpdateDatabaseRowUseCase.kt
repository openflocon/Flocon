package io.github.openflocon.domain.database.usecase

import io.github.openflocon.domain.database.models.DatabaseColumnAffinity
import io.github.openflocon.domain.database.models.DatabaseExecuteSqlResponseDomainModel
import io.github.openflocon.domain.database.models.DatabaseRowEditTargetDomainModel
import io.github.openflocon.domain.database.models.DatabaseRowUpdateResultDomainModel
import io.github.openflocon.domain.database.repository.DatabaseRepository
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase

/** Writes the edited values of a row on the device. The query is not saved in the history. */
class UpdateDatabaseRowUseCase(
    private val databaseRepository: DatabaseRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val buildUpdateRowQueryUseCase: BuildUpdateRowQueryUseCase,
) {
    suspend operator fun invoke(
        target: DatabaseRowEditTargetDomainModel,
        original: List<String?>,
        edited: List<String?>,
    ): DatabaseRowUpdateResultDomainModel {
        val current = getCurrentDeviceIdAndPackageNameUseCase()
        if (current == null || current.deviceId != target.deviceId || current.packageName != target.packageName) {
            return DatabaseRowUpdateResultDomainModel.DeviceChanged
        }
        findInvalidNumber(target.columns, original = original, edited = edited)?.let {
            return DatabaseRowUpdateResultDomainModel.InvalidNumber(it)
        }
        val query = buildUpdateRowQueryUseCase(target = target, original = original, edited = edited)
            ?: return DatabaseRowUpdateResultDomainModel.Failed("nothing to update")

        return databaseRepository.executeQuery(
            deviceIdAndPackageName = current,
            databaseId = target.databaseId,
            query = query,
        ).fold(
            doOnFailure = { DatabaseRowUpdateResultDomainModel.Failed(it.message ?: it.toString()) },
            doOnSuccess = { it.toRowUpdateResult() },
        )
    }
}

internal fun DatabaseExecuteSqlResponseDomainModel.toRowUpdateResult(): DatabaseRowUpdateResultDomainModel = when (this) {
    is DatabaseExecuteSqlResponseDomainModel.UpdateDelete ->
        if (affectedCount > 0) {
            DatabaseRowUpdateResultDomainModel.Updated
        } else {
            DatabaseRowUpdateResultDomainModel.RowNotFound
        }

    DatabaseExecuteSqlResponseDomainModel.RawSuccess -> DatabaseRowUpdateResultDomainModel.Updated

    is DatabaseExecuteSqlResponseDomainModel.Error -> DatabaseRowUpdateResultDomainModel.Failed(message)

    is DatabaseExecuteSqlResponseDomainModel.Insert,
    is DatabaseExecuteSqlResponseDomainModel.Select,
    -> DatabaseRowUpdateResultDomainModel.Failed("unexpected response")
}

/**
 * The first changed column which holds numbers and got something else: SQLite would store the text,
 * which the app may not be able to read. `NUMERIC` columns are not checked, as dates are often stored in them.
 */
internal fun findInvalidNumber(
    columns: List<DatabaseRowEditTargetDomainModel.Column>,
    original: List<String?>,
    edited: List<String?>,
): String? = columns.withIndex().firstOrNull { (index, column) ->
    val value = edited.getOrNull(index)
    value != null &&
        value != original.getOrNull(index) &&
        when (column.affinity) {
            DatabaseColumnAffinity.Integer -> !INTEGER.matches(value) || value.toLongOrNull() == null
            DatabaseColumnAffinity.Real -> !REAL.matches(value)
            else -> false
        }
}?.value?.name

private val INTEGER = Regex("""[+-]?\d+""")
private val REAL = Regex("""[+-]?(\d+(\.\d*)?|\.\d+)([eE][+-]?\d+)?""")

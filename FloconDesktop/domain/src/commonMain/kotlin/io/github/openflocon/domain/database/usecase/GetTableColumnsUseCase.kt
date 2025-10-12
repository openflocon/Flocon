package io.github.openflocon.domain.database.usecase

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.database.models.DatabaseExecuteSqlResponseDomainModel
import io.github.openflocon.domain.database.models.DatabaseTableDomainModel
import io.github.openflocon.domain.database.models.DeviceDataBaseDomainModel
import io.github.openflocon.domain.database.repository.DatabaseRepository
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import javax.swing.table.TableColumn

class GetTableColumnsUseCase(
    private val databaseRepository: DatabaseRepository,
) {
    suspend operator fun invoke(
        tableName: String,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: String
    ): Either<Throwable, Unit> {
        return databaseRepository.executeQuery(
            deviceIdAndPackageName = deviceIdAndPackageName,
            databaseId = databaseId,
            query = "PRAGMA table_info($tableName)",
        ).mapSuccess {
            val columns = extractTableColumns(it)
            DatabaseTableDomainModel(
                tableName = tableName,
                columns = columns,
            )
        }.alsoSuccess {
            databaseRepository.saveTable(
                deviceIdAndPackageName = deviceIdAndPackageName,
                databaseId = databaseId,
                table = it,
            )
        }.mapSuccess {  }
    }



    private fun extractTableColumns(result: DatabaseExecuteSqlResponseDomainModel) : List<DatabaseTableDomainModel.Column> {
        return (result as? DatabaseExecuteSqlResponseDomainModel.Select)?.let {
            it.values.mapNotNull {
                //val id = it.getOrNull(0)
                val name = it.getOrNull(1)
                val type = it.getOrNull(2)
                val notnull = it.getOrNull(3)
                //val dflt_value = it.getOrNull(4)
                val primaryKey = it.getOrNull(5)
                if(name != null && type != null) {
                    DatabaseTableDomainModel.Column(
                        name = name,
                        type = type,
                        nullable = notnull == "0",
                        primaryKey = primaryKey == "1",
                    )
                } else null
            }
        } ?: emptyList()
    }
}

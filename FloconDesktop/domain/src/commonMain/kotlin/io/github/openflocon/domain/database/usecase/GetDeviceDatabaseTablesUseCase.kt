package io.github.openflocon.domain.database.usecase

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.database.models.DatabaseExecuteSqlResponseDomainModel
import io.github.openflocon.domain.database.repository.DatabaseRepository
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope

class GetDeviceDatabaseTablesUseCase(
    private val databaseRepository: DatabaseRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val getCurrentDeviceSelectedDatabaseUseCase: GetCurrentDeviceSelectedDatabaseUseCase,
    private val getTableColumnsUseCase: GetTableColumnsUseCase,
) {
    suspend operator fun invoke(): Either<Throwable, Unit> {
        val deviceIdAndPackageName = getCurrentDeviceIdAndPackageNameUseCase()
            ?: return Failure(Throwable("no current device"))
        val database = getCurrentDeviceSelectedDatabaseUseCase()
            ?: return Failure(Throwable("no selected database"))

        return databaseRepository.executeQuery(
            deviceIdAndPackageName = deviceIdAndPackageName,
            databaseId = database.id,
            query = "SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%' AND name != 'android_metadata' AND name != 'room_master_table' ORDER BY name",
        ).mapSuccess {
            extractTables(it)
        }.alsoSuccess {
            databaseRepository.removeTablesNotPresentAnymore(
                deviceIdAndPackageName = deviceIdAndPackageName,
                databaseId = database.id,
                tablesNames = it,
            )
        }.alsoSuccess { tables ->
            supervisorScope {
                tables.map {
                    async { getTableColumnsUseCase(
                        tableName = it,
                        deviceIdAndPackageName = deviceIdAndPackageName,
                        database = database,
                    ) }
                }.awaitAll()
            }
        }.mapSuccess {  }
    }

    private fun extractTables(result: DatabaseExecuteSqlResponseDomainModel): List<String> {
        return (result as? DatabaseExecuteSqlResponseDomainModel.Select)?.let {
            it.values.mapNotNull { values ->
                values.firstOrNull()
            }
        } ?: emptyList()
    }
}

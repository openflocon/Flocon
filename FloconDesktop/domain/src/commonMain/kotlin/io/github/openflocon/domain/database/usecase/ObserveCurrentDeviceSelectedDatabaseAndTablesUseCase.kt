package io.github.openflocon.domain.database.usecase

import io.github.openflocon.domain.database.models.DatabaseAndTablesDomainModel
import io.github.openflocon.domain.database.models.DeviceDataBaseDomainModel
import io.github.openflocon.domain.database.repository.DatabaseRepository
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class ObserveCurrentDeviceSelectedDatabaseAndTablesUseCase(
    private val databaseRepository: DatabaseRepository,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
) {
    operator fun invoke(database: DeviceDataBaseDomainModel): Flow<DatabaseAndTablesDomainModel?> =
        observeCurrentDeviceIdAndPackageNameUseCase().flatMapLatest { deviceIdAndPackageName ->
            if (deviceIdAndPackageName == null) {
                flowOf(null)
            } else {
                databaseRepository.observe(
                    deviceIdAndPackageName = deviceIdAndPackageName,
                    databaseId = database.id
                ).map { tables ->
                    DatabaseAndTablesDomainModel(
                        database = database,
                        tables = tables,
                    )
                }
            }
        }.distinctUntilChanged()
}

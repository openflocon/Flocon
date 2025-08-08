package io.github.openflocon.flocondesktop.features.database.domain

import io.github.openflocon.flocondesktop.core.domain.device.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.flocondesktop.features.database.domain.model.DeviceDataBaseDomainModel
import io.github.openflocon.flocondesktop.features.database.domain.repository.DatabaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveCurrentDeviceSelectedDatabaseUseCase(
    private val databaseRepository: DatabaseRepository,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
) {
    operator fun invoke(): Flow<DeviceDataBaseDomainModel?> = observeCurrentDeviceIdAndPackageNameUseCase().flatMapLatest { model ->
        if (model == null) {
            flowOf(null)
        } else {
            databaseRepository.observeSelectedDeviceDatabase(deviceIdAndPackageName = model)
        }
    }
}

package io.github.openflocon.flocondesktop.features.database.domain

import io.github.openflocon.flocondesktop.core.domain.device.ObserveCurrentDeviceIdAndPackageNameUseCase
import com.flocon.library.domain.models.DeviceDataBaseDomainModel
import io.github.openflocon.flocondesktop.features.database.domain.repository.DatabaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveDeviceDatabaseUseCase(
    private val databaseRepository: DatabaseRepository,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
) {
    operator fun invoke(): Flow<List<DeviceDataBaseDomainModel>> = observeCurrentDeviceIdAndPackageNameUseCase().flatMapLatest { model ->
        if (model == null) {
            flowOf(emptyList())
        } else {
            databaseRepository.observeDeviceDatabases(deviceIdAndPackageName = model)
        }
    }
}

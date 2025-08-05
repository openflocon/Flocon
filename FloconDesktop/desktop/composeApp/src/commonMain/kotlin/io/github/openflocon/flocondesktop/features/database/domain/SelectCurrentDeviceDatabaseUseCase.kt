package io.github.openflocon.flocondesktop.features.database.domain

import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import io.github.openflocon.flocondesktop.features.database.domain.model.DeviceDataBaseId
import io.github.openflocon.flocondesktop.features.database.domain.repository.DatabaseRepository

class SelectCurrentDeviceDatabaseUseCase(
    private val databaseRepository: DatabaseRepository,
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
) {
    operator fun invoke(databaseId: DeviceDataBaseId) {
        val currentDevice = getCurrentDeviceIdUseCase() ?: return
        databaseRepository.selectDeviceDatabase(
            deviceId = currentDevice,
            databaseId = databaseId,
        )
    }
}

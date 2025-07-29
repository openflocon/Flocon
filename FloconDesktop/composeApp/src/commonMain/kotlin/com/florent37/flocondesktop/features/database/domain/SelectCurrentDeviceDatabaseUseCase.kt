package com.florent37.flocondesktop.features.database.domain

import com.florent37.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import com.florent37.flocondesktop.features.database.domain.model.DeviceDataBaseId
import com.florent37.flocondesktop.features.database.domain.repository.DatabaseRepository

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

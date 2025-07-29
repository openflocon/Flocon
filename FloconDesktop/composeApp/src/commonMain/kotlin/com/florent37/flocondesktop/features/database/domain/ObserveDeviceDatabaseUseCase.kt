package com.florent37.flocondesktop.features.database.domain

import com.florent37.flocondesktop.core.domain.device.ObserveCurrentDeviceIdUseCase
import com.florent37.flocondesktop.features.database.domain.model.DeviceDataBaseDomainModel
import com.florent37.flocondesktop.features.database.domain.repository.DatabaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveDeviceDatabaseUseCase(
    private val databaseRepository: DatabaseRepository,
    private val observeCurrentDeviceIdUseCase: ObserveCurrentDeviceIdUseCase,
) {
    operator fun invoke(): Flow<List<DeviceDataBaseDomainModel>> = observeCurrentDeviceIdUseCase().flatMapLatest { deviceId ->
        if (deviceId == null) {
            flowOf(emptyList())
        } else {
            databaseRepository.observeDeviceDatabases(deviceId = deviceId)
        }
    }
}

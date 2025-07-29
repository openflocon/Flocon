package com.florent37.flocondesktop.features.database.domain

import com.florent37.flocondesktop.core.domain.device.ObserveCurrentDeviceIdUseCase
import com.florent37.flocondesktop.features.database.domain.repository.DatabaseRepository
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveLastSuccessQueriesUseCase(
    private val observeCurrentDeviceIdUseCase: ObserveCurrentDeviceIdUseCase,
    private val databaseRepository: DatabaseRepository,
) {
    operator fun invoke() = observeCurrentDeviceIdUseCase().flatMapLatest { deviceId ->
        if (deviceId == null) {
            flowOf(emptyList())
        } else {
            databaseRepository.observeSelectedDeviceDatabase(deviceId = deviceId).flatMapLatest { database ->
                if (database == null) {
                    flowOf(emptyList())
                } else {
                    databaseRepository.observeLastSuccessQuery(deviceId = deviceId, databaseId = database.id)
                }
            }
        }
    }
}

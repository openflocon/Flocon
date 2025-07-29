package com.florent37.flocondesktop.features.table.domain

import com.florent37.flocondesktop.core.domain.device.ObserveCurrentDeviceIdUseCase
import com.florent37.flocondesktop.features.table.domain.model.TableIdentifierDomainModel
import com.florent37.flocondesktop.features.table.domain.repository.TableRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveDeviceTablesUseCase(
    private val tableRepository: TableRepository,
    private val observeCurrentDeviceIdUseCase: ObserveCurrentDeviceIdUseCase,
) {
    operator fun invoke(): Flow<List<TableIdentifierDomainModel>> = observeCurrentDeviceIdUseCase().flatMapLatest { deviceId ->
        if (deviceId == null) {
            flowOf(emptyList())
        } else {
            tableRepository.observeDeviceTables(deviceId = deviceId)
        }
    }
}

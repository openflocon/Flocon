package com.florent37.flocondesktop.features.table.domain

import com.florent37.flocondesktop.core.domain.device.ObserveCurrentDeviceIdUseCase
import com.florent37.flocondesktop.features.table.domain.model.TableDomainModel
import com.florent37.flocondesktop.features.table.domain.repository.TableRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveCurrentDeviceTableContentUseCase(
    private val observeCurrentDeviceIdUseCase: ObserveCurrentDeviceIdUseCase,
    private val tableRepository: TableRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<TableDomainModel?> = observeCurrentDeviceIdUseCase()
        .flatMapLatest { deviceId ->
            if (deviceId == null) {
                flowOf(null)
            } else {
                tableRepository
                    .observeSelectedDeviceTable(deviceId = deviceId)
                    .flatMapLatest { selectedTable ->
                        if (selectedTable == null) {
                            flowOf(null)
                        } else {
                            tableRepository.observeTable(
                                deviceId = deviceId,
                                tableId = selectedTable.id,
                            )
                        }
                    }
            }
        }
}

package com.florent37.flocondesktop.features.table.domain

import com.florent37.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import com.florent37.flocondesktop.features.table.domain.model.TableId
import com.florent37.flocondesktop.features.table.domain.repository.TableRepository

class SelectCurrentDeviceTableUseCase(
    private val tableRepository: TableRepository,
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
) {
    suspend operator fun invoke(tableId: TableId) {
        val currentDevice = getCurrentDeviceIdUseCase() ?: return
        tableRepository.selectDeviceTable(
            deviceId = currentDevice,
            tableId = tableId,
        )
    }
}

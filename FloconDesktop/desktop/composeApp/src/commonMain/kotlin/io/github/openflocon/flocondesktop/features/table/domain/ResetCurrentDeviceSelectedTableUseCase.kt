package io.github.openflocon.flocondesktop.features.table.domain

import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import io.github.openflocon.flocondesktop.features.table.domain.repository.TableRepository

class ResetCurrentDeviceSelectedTableUseCase(
    private val tableRepository: TableRepository,
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
    private val getCurrentDeviceSelectedTableUseCase: GetCurrentDeviceSelectedTableUseCase,
) {
    suspend operator fun invoke() {
        val deviceId = getCurrentDeviceIdUseCase() ?: return
        val tableId = getCurrentDeviceSelectedTableUseCase() ?: return
        tableRepository.deleteTable(deviceId = deviceId, tableId = tableId)
    }
}

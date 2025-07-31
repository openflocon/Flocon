package io.github.openflocon.flocondesktop.features.table.domain

import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import io.github.openflocon.flocondesktop.features.table.domain.model.TableId
import io.github.openflocon.flocondesktop.features.table.domain.repository.TableRepository

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

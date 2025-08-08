package io.github.openflocon.flocondesktop.features.table.domain

import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.flocondesktop.features.table.domain.repository.TableRepository

class ResetCurrentDeviceSelectedTableUseCase(
    private val tableRepository: TableRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val getCurrentDeviceSelectedTableUseCase: GetCurrentDeviceSelectedTableUseCase,
) {
    suspend operator fun invoke() {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return
        val tableId = getCurrentDeviceSelectedTableUseCase() ?: return

        tableRepository.deleteTable(deviceIdAndPackageName = current, tableId = tableId)
    }
}

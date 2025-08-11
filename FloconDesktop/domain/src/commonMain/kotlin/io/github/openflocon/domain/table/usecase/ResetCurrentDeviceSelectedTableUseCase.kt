package io.github.openflocon.domain.table.usecase

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.table.repository.TableRepository

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

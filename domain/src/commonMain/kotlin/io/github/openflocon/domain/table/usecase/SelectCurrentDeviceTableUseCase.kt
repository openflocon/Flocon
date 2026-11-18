package io.github.openflocon.domain.table.usecase

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.table.models.TableId
import io.github.openflocon.domain.table.repository.TableRepository

class SelectCurrentDeviceTableUseCase(
    private val tableRepository: TableRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
) {
    suspend operator fun invoke(tableId: TableId) {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return

        tableRepository.selectDeviceTable(
            deviceIdAndPackageName = current,
            tableId = tableId,
        )
    }
}

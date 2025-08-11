package io.github.openflocon.flocondesktop.features.table.domain

import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdAndPackageNameUseCase
import com.flocon.library.domain.models.TableId
import io.github.openflocon.flocondesktop.features.table.domain.repository.TableRepository

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

package io.github.openflocon.domain.table.usecase

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.table.repository.TableRepository

class RemoveTableItemUseCase(
    private val tableRepository: TableRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
) {
    suspend operator fun invoke(itemId: String) {
        getCurrentDeviceIdAndPackageNameUseCase()?.let { deviceIdAndPackageName ->
            tableRepository.deleteItem(
                deviceIdAndPackageName = deviceIdAndPackageName,
                itemId = itemId
            )
        }
    }
}

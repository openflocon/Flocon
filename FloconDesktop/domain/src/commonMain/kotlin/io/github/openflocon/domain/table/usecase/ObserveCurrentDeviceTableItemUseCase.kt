package io.github.openflocon.domain.table.usecase

import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.table.models.TableDomainModel
import io.github.openflocon.domain.table.repository.TableRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveCurrentDeviceTableItemUseCase(
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    private val tableRepository: TableRepository,
) {
    /** The table the item belongs to, containing only this item, whatever the table selected in the Tables screen */
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(itemId: String): Flow<TableDomainModel?> = observeCurrentDeviceIdAndPackageNameUseCase()
        .flatMapLatest { current ->
            if (current == null) {
                flowOf(null)
            } else {
                tableRepository.observeTableItem(deviceIdAndPackageName = current, itemId = itemId)
            }
        }
}

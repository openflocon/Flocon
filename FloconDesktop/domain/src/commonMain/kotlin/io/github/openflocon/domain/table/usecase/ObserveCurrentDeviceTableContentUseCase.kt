package io.github.openflocon.domain.table.usecase

import io.github.openflocon.flocondesktop.core.domain.device.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.table.models.TableDomainModel
import io.github.openflocon.domain.table.repository.TableRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveCurrentDeviceTableContentUseCase(
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    private val tableRepository: TableRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<TableDomainModel?> = observeCurrentDeviceIdAndPackageNameUseCase()
        .flatMapLatest { current ->
            if (current == null) {
                flowOf(null)
            } else {
                tableRepository.observeSelectedDeviceTable(deviceIdAndPackageName = current)
                    .flatMapLatest { selectedTable ->
                        if (selectedTable == null) {
                            flowOf(null)
                        } else {
                            tableRepository.observeTable(
                                deviceIdAndPackageName = current,
                                tableId = selectedTable.id,
                            )
                        }
                    }
            }
        }
}

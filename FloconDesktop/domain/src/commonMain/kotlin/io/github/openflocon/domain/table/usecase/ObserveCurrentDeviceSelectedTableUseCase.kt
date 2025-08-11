package io.github.openflocon.domain.table.usecase

import io.github.openflocon.flocondesktop.core.domain.device.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.table.models.TableIdentifierDomainModel
import io.github.openflocon.domain.table.repository.TableRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveCurrentDeviceSelectedTableUseCase(
    private val tableRepository: TableRepository,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
) {
    operator fun invoke(): Flow<TableIdentifierDomainModel?> = observeCurrentDeviceIdAndPackageNameUseCase().flatMapLatest { current ->
        if (current == null) {
            flowOf(null)
        } else {
            tableRepository.observeSelectedDeviceTable(deviceIdAndPackageName = current)
        }
    }
}

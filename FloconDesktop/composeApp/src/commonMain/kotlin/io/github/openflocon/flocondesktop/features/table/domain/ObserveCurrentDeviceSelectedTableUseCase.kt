package io.github.openflocon.flocondesktop.features.table.domain

import com.florent37.flocondesktop.core.domain.device.ObserveCurrentDeviceIdUseCase
import com.florent37.flocondesktop.features.table.domain.model.TableIdentifierDomainModel
import com.florent37.flocondesktop.features.table.domain.repository.TableRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveCurrentDeviceSelectedTableUseCase(
    private val tableRepository: TableRepository,
    private val observeCurrentDeviceIdUseCase: ObserveCurrentDeviceIdUseCase,
) {
    operator fun invoke(): Flow<TableIdentifierDomainModel?> = observeCurrentDeviceIdUseCase().flatMapLatest { deviceId ->
        if (deviceId == null) {
            flowOf(null)
        } else {
            tableRepository.observeSelectedDeviceTable(deviceId = deviceId)
        }
    }
}

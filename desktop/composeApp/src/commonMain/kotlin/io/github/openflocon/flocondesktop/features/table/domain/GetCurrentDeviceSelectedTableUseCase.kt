package io.github.openflocon.flocondesktop.features.table.domain

import io.github.openflocon.flocondesktop.features.table.domain.model.TableIdentifierDomainModel
import kotlinx.coroutines.flow.firstOrNull

class GetCurrentDeviceSelectedTableUseCase(
    private val observeCurrentDeviceSelectedTableUseCase: ObserveCurrentDeviceSelectedTableUseCase,
) {
    suspend operator fun invoke(): TableIdentifierDomainModel? = observeCurrentDeviceSelectedTableUseCase().firstOrNull()
}

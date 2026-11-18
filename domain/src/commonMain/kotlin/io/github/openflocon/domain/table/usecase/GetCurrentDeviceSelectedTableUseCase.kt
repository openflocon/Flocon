package io.github.openflocon.domain.table.usecase

import io.github.openflocon.domain.table.models.TableIdentifierDomainModel
import kotlinx.coroutines.flow.firstOrNull

class GetCurrentDeviceSelectedTableUseCase(
    private val observeCurrentDeviceSelectedTableUseCase: ObserveCurrentDeviceSelectedTableUseCase,
) {
    suspend operator fun invoke(): TableIdentifierDomainModel? = observeCurrentDeviceSelectedTableUseCase().firstOrNull()
}

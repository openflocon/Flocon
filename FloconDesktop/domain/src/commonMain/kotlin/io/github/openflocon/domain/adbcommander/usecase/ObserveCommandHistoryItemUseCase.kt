package io.github.openflocon.domain.adbcommander.usecase

import io.github.openflocon.domain.adbcommander.models.AdbCommandHistoryDomainModel
import io.github.openflocon.domain.adbcommander.repository.AdbCommanderRepository
import kotlinx.coroutines.flow.Flow

class ObserveCommandHistoryItemUseCase(
    private val adbCommanderRepository: AdbCommanderRepository,
) {
    operator fun invoke(id: Long): Flow<AdbCommandHistoryDomainModel?> = adbCommanderRepository.observeHistoryItem(id)
}

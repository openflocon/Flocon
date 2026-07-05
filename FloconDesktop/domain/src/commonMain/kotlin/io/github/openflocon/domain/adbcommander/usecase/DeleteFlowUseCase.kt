package io.github.openflocon.domain.adbcommander.usecase

import io.github.openflocon.domain.adbcommander.repository.AdbCommanderRepository

class DeleteFlowUseCase(
    private val adbCommanderRepository: AdbCommanderRepository,
) {
    suspend operator fun invoke(id: Long) {
        adbCommanderRepository.deleteFlow(id)
    }
}

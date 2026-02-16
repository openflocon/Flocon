package io.github.openflocon.domain.commands.usecase

import io.github.openflocon.domain.commands.models.AdbCommand
import io.github.openflocon.domain.commands.repository.AdbCommandRepository

class DeleteAdbCommandUseCase internal constructor(
    private val adbCommandRepository: AdbCommandRepository
) {
    suspend operator fun invoke(command: AdbCommand) {
        adbCommandRepository.delete(command)
    }
}

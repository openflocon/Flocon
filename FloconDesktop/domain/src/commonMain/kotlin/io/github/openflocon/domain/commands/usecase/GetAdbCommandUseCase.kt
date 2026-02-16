package io.github.openflocon.domain.commands.usecase

import io.github.openflocon.domain.commands.models.AdbCommand
import io.github.openflocon.domain.commands.repository.AdbCommandRepository

class GetAdbCommandUseCase
internal constructor(private val adbCommandRepository: AdbCommandRepository) {
    suspend operator fun invoke(id: Long): AdbCommand? {
        return adbCommandRepository.get(id)
    }
}

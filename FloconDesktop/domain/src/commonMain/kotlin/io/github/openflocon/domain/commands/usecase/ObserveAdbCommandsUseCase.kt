package io.github.openflocon.domain.commands.usecase

import io.github.openflocon.domain.commands.models.AdbCommand
import io.github.openflocon.domain.commands.repository.AdbCommandRepository
import kotlinx.coroutines.flow.Flow

class ObserveAdbCommandsUseCase internal constructor(
    private val adbCommandRepository: AdbCommandRepository
) {
    operator fun invoke(): Flow<List<AdbCommand>> = adbCommandRepository.getAll()
}

package io.github.openflocon.domain.messages.usecase

import io.github.openflocon.domain.messages.repository.MessagesRepository

class StopServerUseCase(
    private val messagesRepository: MessagesRepository,
) {
    operator fun invoke() = messagesRepository.stopServer()
}

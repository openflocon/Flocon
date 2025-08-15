package io.github.openflocon.domain.messages.usecase

import io.github.openflocon.domain.messages.repository.MessagesRepository

class StartServerUseCase(
    private val messagesRepository: MessagesRepository,
) {
    operator fun invoke() = messagesRepository.startServer()
}

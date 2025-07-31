package io.github.openflocon.flocondesktop.messages.domain

import io.github.openflocon.flocondesktop.messages.domain.repository.MessagesRepository

class StartServerUseCase(
    private val messagesRepository: MessagesRepository,
) {
    operator fun invoke() = messagesRepository.startServer()
}

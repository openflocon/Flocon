package io.github.openflocon.flocondesktop.messages.domain.repository

import io.github.openflocon.flocondesktop.FloconIncomingMessageDataModel
import kotlinx.coroutines.flow.Flow

interface MessagesRepository {
    fun listenMessages(): Flow<FloconIncomingMessageDataModel>

    fun startServer()
}

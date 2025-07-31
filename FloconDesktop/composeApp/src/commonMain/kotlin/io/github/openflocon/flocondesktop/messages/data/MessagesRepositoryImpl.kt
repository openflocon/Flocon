package io.github.openflocon.flocondesktop.messages.data

import io.github.openflocon.flocondesktop.FloconIncomingMessageDataModel
import io.github.openflocon.flocondesktop.Server
import io.github.openflocon.flocondesktop.messages.domain.repository.MessagesRepository
import kotlinx.coroutines.flow.Flow

class MessagesRepositoryImpl(
    private val server: Server,
) : MessagesRepository {
    override fun startServer() {
        server.start()
    }

    override fun listenMessages(): Flow<FloconIncomingMessageDataModel> = server.receivedMessages
}

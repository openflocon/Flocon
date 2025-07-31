package io.github.openflocon.flocondesktop.messages.data

import com.florent37.flocondesktop.FloconIncomingMessageDataModel
import com.florent37.flocondesktop.Server
import com.florent37.flocondesktop.messages.domain.repository.MessagesRepository
import kotlinx.coroutines.flow.Flow

class MessagesRepositoryImpl(
    private val server: Server,
) : MessagesRepository {
    override fun startServer() {
        server.start()
    }

    override fun listenMessages(): Flow<FloconIncomingMessageDataModel> = server.receivedMessages
}

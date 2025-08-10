package io.github.openflocon.flocondesktop.messages.data

import com.flocon.data.remote.models.FloconIncomingMessageDataModel
import com.flocon.data.remote.server.Server
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

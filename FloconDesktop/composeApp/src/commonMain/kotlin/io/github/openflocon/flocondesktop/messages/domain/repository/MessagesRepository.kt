package io.github.openflocon.flocondesktop.messages.domain.repository

import com.florent37.flocondesktop.FloconIncomingMessageDataModel
import kotlinx.coroutines.flow.Flow

interface MessagesRepository {
    fun listenMessages(): Flow<FloconIncomingMessageDataModel>

    fun startServer()
}

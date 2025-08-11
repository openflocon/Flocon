package io.github.openflocon.flocondesktop.messages.domain.repository

import com.flocon.data.remote.models.FloconIncomingMessageDataModel
import kotlinx.coroutines.flow.Flow

interface MessagesRepository {
    fun listenMessages(): Flow<FloconIncomingMessageDataModel>

    fun startServer()
}

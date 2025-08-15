package io.github.openflocon.domain.messages.repository

import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import kotlinx.coroutines.flow.Flow

interface MessagesRepository {
    fun listenMessages(): Flow<FloconIncomingMessageDomainModel>

    fun startServer()
}

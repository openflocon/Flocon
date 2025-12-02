package io.github.openflocon.data.core.messages.repository

import io.github.openflocon.data.core.messages.datasource.MessageRemoteDataSource
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.messages.models.FloconReceivedFileDomainModel
import io.github.openflocon.domain.messages.repository.MessagesRepository
import kotlinx.coroutines.flow.Flow

internal class MessagesRepositoryImpl(
    private val remote: MessageRemoteDataSource,
) : MessagesRepository {

    override fun startServer() {
        remote.startServer()
    }

    override fun listenMessages(): Flow<FloconIncomingMessageDomainModel> = remote.listenMessages()

    override fun listenReceivedFiles(): Flow<FloconReceivedFileDomainModel> = remote.listenReceivedFiles()
}

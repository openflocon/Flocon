package com.flocon.data.remote.messages.datasource

import com.flocon.data.remote.messages.mapper.toDomain
import com.flocon.data.remote.models.FloconIncomingMessageDataModel
import com.flocon.data.remote.server.Server
import io.github.openflocon.data.core.messages.datasource.MessageRemoteDataSource
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.messages.models.FloconReceivedFileDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class MessageRemoteDataSourceImpl(
    private val server: Server,
) : MessageRemoteDataSource {

    override fun startServer() {
        server.startWebsocket()
        server.starHttp()
    }

    override fun listenMessages(): Flow<FloconIncomingMessageDomainModel> = server.receivedMessages
        .map(FloconIncomingMessageDataModel::toDomain)

    override fun listenReceivedFiles(): Flow<FloconReceivedFileDomainModel> = server.receivedFiles
}

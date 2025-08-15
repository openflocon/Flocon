package com.flocon.data.remote.messages.datasource

import com.flocon.data.remote.messages.mapper.toDomain
import com.flocon.data.remote.models.FloconIncomingMessageDataModel
import com.flocon.data.remote.server.Server
import io.github.openflocon.data.core.messages.datasource.MessageRemoteDataSource
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class MessageRemoteDataSourceImpl(
    private val server: Server
) : MessageRemoteDataSource {

    override fun startServer() {
        server.start()
    }

    override fun listenMessages(): Flow<FloconIncomingMessageDomainModel> {
        return server.receivedMessages
            .map(FloconIncomingMessageDataModel::toDomain)
    }
}

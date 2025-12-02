package io.github.openflocon.data.core.messages.datasource

import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.messages.models.FloconReceivedFileDomainModel
import kotlinx.coroutines.flow.Flow

interface MessageRemoteDataSource {

    fun startServer()

    fun listenMessages(): Flow<FloconIncomingMessageDomainModel>
    fun listenReceivedFiles(): Flow<FloconReceivedFileDomainModel>
}

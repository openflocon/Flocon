package io.github.openflocon.flocondesktop.messages.domain.repository.sub

import com.florent37.flocondesktop.FloconIncomingMessageDataModel

interface MessagesReceiverRepository {
    val pluginName: List<String>
    suspend fun onMessageReceived(
        deviceId: String,
        message: FloconIncomingMessageDataModel,
    )
}

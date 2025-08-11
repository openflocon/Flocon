package io.github.openflocon.flocondesktop.messages.domain.repository.sub

import com.flocon.data.remote.models.FloconIncomingMessageDataModel


interface MessagesReceiverRepository {
    val pluginName: List<String>
    suspend fun onMessageReceived(
        deviceId: String,
        message: FloconIncomingMessageDataModel,
    )
}

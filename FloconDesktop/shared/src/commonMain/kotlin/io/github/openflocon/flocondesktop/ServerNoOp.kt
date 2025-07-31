package io.github.openflocon.flocondesktop

import kotlinx.coroutines.flow.MutableSharedFlow

class ServerNoOp : Server {
    override val receivedMessages = MutableSharedFlow<FloconIncomingMessageDataModel>()

    override fun start(port: Int) {
        // no op
    }

    override suspend fun sendMessageToClient(
        deviceId: DeviceId,
        message: FloconOutgoingMessageDataModel,
    ) {
        // no op
    }

    override fun stop() {
        // no op
    }
}

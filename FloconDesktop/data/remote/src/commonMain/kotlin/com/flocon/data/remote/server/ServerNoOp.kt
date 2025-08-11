package com.flocon.data.remote.server

import com.flocon.data.remote.models.FloconDeviceIdAndPackageNameDataModel
import com.flocon.data.remote.models.FloconIncomingMessageDataModel
import com.flocon.data.remote.models.FloconOutgoingMessageDataModel
import kotlinx.coroutines.flow.MutableSharedFlow

class ServerNoOp : Server {
    override val receivedMessages = MutableSharedFlow<FloconIncomingMessageDataModel>()

    override fun start(port: Int) {
        // no op
    }

    override suspend fun sendMessageToClient(deviceIdAndPackageName: FloconDeviceIdAndPackageNameDataModel, message: FloconOutgoingMessageDataModel) {
        // no op
    }

    override fun stop() {
        // no op
    }
}

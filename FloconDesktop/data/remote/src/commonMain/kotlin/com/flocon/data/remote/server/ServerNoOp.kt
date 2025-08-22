package com.flocon.data.remote.server

import com.flocon.data.remote.models.FloconDeviceIdAndPackageNameDataModel
import com.flocon.data.remote.models.FloconIncomingMessageDataModel
import com.flocon.data.remote.models.FloconOutgoingMessageDataModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf

class ServerNoOp : Server {
    override val receivedMessages = MutableSharedFlow<FloconIncomingMessageDataModel>()

    override fun start(port: Int) {
        // no op
    }

    override suspend fun sendMessageToClient(deviceIdAndPackageName: FloconDeviceIdAndPackageNameDataModel, message: FloconOutgoingMessageDataModel) {
        // no op
    }

    override val activeDevices: Flow<Set<FloconDeviceIdAndPackageNameDataModel>> = flowOf(emptySet())

    override fun stop() {
        // no op
    }
}

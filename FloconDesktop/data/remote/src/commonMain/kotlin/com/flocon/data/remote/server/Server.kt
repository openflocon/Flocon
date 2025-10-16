package com.flocon.data.remote.server

import com.flocon.data.remote.models.FloconDeviceIdAndPackageNameDataModel
import com.flocon.data.remote.models.FloconIncomingMessageDataModel
import com.flocon.data.remote.models.FloconOutgoingMessageDataModel
import io.github.openflocon.domain.Constant
import io.github.openflocon.domain.messages.models.FloconReceivedFileDomainModel
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface Server {
    fun startWebsocket(port: Int = Constant.SERVER_WEBSOCKET_PORT)

    fun starHttp(port: Int = Constant.SERVER_HTTP_PORT)

    fun stop()

    val receivedMessages: Flow<FloconIncomingMessageDataModel>

    suspend fun sendMessageToClient(
        deviceIdAndPackageName: FloconDeviceIdAndPackageNameDataModel,
        message: FloconOutgoingMessageDataModel,
    )

    val activeDevices: Flow<Set<FloconDeviceIdAndPackageNameDataModel>>
    val receivedFiles: Flow<FloconReceivedFileDomainModel>
}

@OptIn(ExperimentalUuidApi::class)
fun newRequestId(): String = Uuid.random().toString()

expect fun getServer(): Server

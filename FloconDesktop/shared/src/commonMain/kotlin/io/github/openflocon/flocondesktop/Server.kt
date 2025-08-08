package io.github.openflocon.flocondesktop

import kotlinx.coroutines.flow.Flow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

val SERVER_PORT: Int = 9023

interface Server {
    fun start(port: Int = SERVER_PORT)

    fun stop()

    val receivedMessages: Flow<FloconIncomingMessageDataModel>

    suspend fun sendMessageToClient(
        deviceIdAndPackageName: FloconDeviceIdAndPackageName,
        message: FloconOutgoingMessageDataModel,
    )
}

@OptIn(ExperimentalUuidApi::class)
fun newRequestId(): String = Uuid.random().toString()

expect fun getServer(): Server

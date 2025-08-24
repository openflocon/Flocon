package com.flocon.data.remote.server

import com.flocon.data.remote.models.FloconDeviceIdAndPackageNameDataModel
import com.flocon.data.remote.models.FloconIncomingMessageDataModel
import com.flocon.data.remote.models.FloconOutgoingMessageDataModel
import io.github.openflocon.domain.Constant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface Server {
    fun start(port: Int = Constant.SERVER_PORT)

    fun stop()

    val receivedMessages: Flow<FloconIncomingMessageDataModel>

    suspend fun sendMessageToClient(
        deviceIdAndPackageName: FloconDeviceIdAndPackageNameDataModel,
        message: FloconOutgoingMessageDataModel,
    )

    val activeDevices: Flow<Set<FloconDeviceIdAndPackageNameDataModel>>
}

@OptIn(ExperimentalUuidApi::class)
fun newRequestId(): String = Uuid.random().toString()

expect fun getServer(): Server

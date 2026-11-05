package com.flocon.data.remote.server

import com.flocon.data.remote.models.FloconDeviceIdAndPackageNameDataModel
import com.flocon.data.remote.models.FloconIncomingMessageDataModel
import com.flocon.data.remote.models.FloconOutgoingMessageDataModel
import io.github.openflocon.domain.messages.models.FloconReceivedFileDomainModel
import io.github.openflocon.domain.network.repository.NetworkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface Server {
    fun startWebsocket(port: Int)

    fun starHttp(port: Int)

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

expect fun getServer(
    json: Json,
    networkRepository: Lazy<NetworkRepository>,
): Server

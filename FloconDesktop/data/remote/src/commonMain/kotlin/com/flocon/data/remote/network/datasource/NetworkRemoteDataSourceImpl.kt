package com.flocon.data.remote.network.datasource

import com.flocon.data.remote.common.safeDecodeFromString
import com.flocon.data.remote.models.FloconOutgoingMessageDataModel
import com.flocon.data.remote.models.toRemote
import com.flocon.data.remote.network.mapper.listToRemote
import com.flocon.data.remote.network.mapper.toDomain
import com.flocon.data.remote.network.mapper.toRemote
import com.flocon.data.remote.network.models.FloconNetworkCallIdDataModel
import com.flocon.data.remote.network.models.FloconNetworkRequestDataModel
import com.flocon.data.remote.network.models.FloconNetworkResponseDataModel
import com.flocon.data.remote.network.models.FloconNetworkWebSocketEvent
import com.flocon.data.remote.network.models.WebsocketMockRemoteModel
import com.flocon.data.remote.network.models.toDomain
import com.flocon.data.remote.server.Server
import io.github.openflocon.data.core.network.datasource.NetworkRemoteDataSource
import io.github.openflocon.domain.Protocol
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.network.models.BadQualityConfigDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallIdDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkResponseOnlyDomainModel
import io.github.openflocon.domain.network.models.MockNetworkDomainModel
import io.github.openflocon.domain.network.models.NetworkWebsocketId
import kotlinx.serialization.json.Json

class NetworkRemoteDataSourceImpl(
    private val server: Server,
    private val json: Json,
) : NetworkRemoteDataSource {

    override suspend fun setupMocks(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        mocks: List<MockNetworkDomainModel>,
    ) {
        server.sendMessageToClient(
            deviceIdAndPackageName = deviceIdAndPackageName.toRemote(),
            message = FloconOutgoingMessageDataModel(
                plugin = Protocol.ToDevice.Network.Plugin,
                method = Protocol.ToDevice.Network.Method.SetupMocks,
                body = Json.Default.encodeToString(
                    listToRemote(mocks),
                ),
            ),
        )
    }

    override suspend fun setupBadNetworkQuality(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        config: BadQualityConfigDomainModel?,
    ) {
        server.sendMessageToClient(
            deviceIdAndPackageName = deviceIdAndPackageName.toRemote(),
            message = FloconOutgoingMessageDataModel(
                plugin = Protocol.ToDevice.Network.Plugin,
                method = Protocol.ToDevice.Network.Method.SetupBadNetworkConfig,
                body = if (config == null || config.isEnabled.not()) {
                    "{}" // empty json to clear the config mobile side
                } else {
                    Json.Default.encodeToString(toRemote(config))
                },
            ),
        )
    }

    override suspend fun sendWebsocketMock(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        websocketId: NetworkWebsocketId,
        message: String,
    ) {
        server.sendMessageToClient(
            deviceIdAndPackageName = deviceIdAndPackageName.toRemote(),
            message = FloconOutgoingMessageDataModel(
                plugin = Protocol.ToDevice.Network.Plugin,
                method = Protocol.ToDevice.Network.Method.WebsocketMockMessage,
                body = Json.Default.encodeToString(
                    WebsocketMockRemoteModel(
                        id = websocketId,
                        message = message,
                    ),
                ),
            ),
        )
    }

    override fun getRequestData(message: FloconIncomingMessageDomainModel): FloconNetworkCallDomainModel? = json.safeDecodeFromString<FloconNetworkRequestDataModel>(message.body)
        ?.let {
            com.flocon.data.remote.network.mapper.toDomain(
                it,
                appInstance = message.appInstance,
            )
        }

    override fun getCallId(message: FloconIncomingMessageDomainModel): FloconNetworkCallIdDomainModel? = json.safeDecodeFromString<FloconNetworkCallIdDataModel>(message.body)
        ?.let(FloconNetworkCallIdDataModel::toDomain)

    override fun getResponseData(message: FloconIncomingMessageDomainModel): FloconNetworkResponseOnlyDomainModel? = json.safeDecodeFromString<FloconNetworkResponseDataModel>(message.body)
        ?.let(FloconNetworkResponseDataModel::toDomain)

    override fun getWebSocketData(message: FloconIncomingMessageDomainModel): FloconNetworkCallDomainModel? = json.safeDecodeFromString<FloconNetworkWebSocketEvent>(message.body)
        ?.let { it.toDomain(appInstance = message.appInstance) }

    override fun getWebsocketClientsIds(message: FloconIncomingMessageDomainModel): List<String> = json.safeDecodeFromString<List<String>>(message.body) ?: emptyList()
}

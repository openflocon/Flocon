package com.flocon.data.remote.network.datasource

import com.flocon.data.remote.common.safeDecodeFromString
import com.flocon.data.remote.models.FloconOutgoingMessageDataModel
import com.flocon.data.remote.models.toRemote
import com.flocon.data.remote.network.mapper.listToRemote
import com.flocon.data.remote.network.models.FloconNetworkCallIdDataModel
import com.flocon.data.remote.network.models.FloconNetworkRequestDataModel
import com.flocon.data.remote.network.models.FloconNetworkResponseDataModel
import com.flocon.data.remote.network.models.toDomain
import com.flocon.data.remote.server.Server
import io.github.openflocon.data.core.network.datasource.NetworkRemoteDataSource
import io.github.openflocon.domain.Protocol
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallIdDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkResponseDomainModel
import io.github.openflocon.domain.network.models.MockNetworkDomainModel
import kotlinx.serialization.json.Json

class NetworkRemoteDataSourceImpl(
    private val server: Server,
    private val json: Json
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

    override fun getRequestData(message: FloconIncomingMessageDomainModel): FloconNetworkCallDomainModel? {
        return json.safeDecodeFromString<FloconNetworkRequestDataModel>(message.body)
            ?.let { com.flocon.data.remote.network.mapper.toDomain(it) }
    }

    override fun getCallId(message: FloconIncomingMessageDomainModel): FloconNetworkCallIdDomainModel? {
        return json.safeDecodeFromString<FloconNetworkCallIdDataModel>(message.body)
            ?.let(FloconNetworkCallIdDataModel::toDomain)
    }

    override fun getResponseData(message: FloconIncomingMessageDomainModel): FloconNetworkResponseDomainModel? {
        return json.safeDecodeFromString<FloconNetworkResponseDataModel>(message.body)
            ?.let(FloconNetworkResponseDataModel::toDomain)
    }
}

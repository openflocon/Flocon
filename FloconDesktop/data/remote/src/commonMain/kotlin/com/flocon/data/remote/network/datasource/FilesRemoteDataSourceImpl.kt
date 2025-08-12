package com.flocon.data.remote.network.datasource

import com.flocon.data.remote.Protocol
import com.flocon.data.remote.models.FloconOutgoingMessageDataModel
import com.flocon.data.remote.models.toRemote
import com.flocon.data.remote.network.mapper.listToRemote
import com.flocon.data.remote.server.Server
import io.github.openflocon.data.core.network.datasource.NetworkRemoteDataSource
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.MockNetworkDomainModel
import kotlinx.serialization.json.Json

class NetworkRemoteDataSourceImpl(
    private val server: Server,
) : NetworkRemoteDataSource {

    override suspend fun setupMocks(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        mocks: List<MockNetworkDomainModel>
    ) {
        server.sendMessageToClient(
            deviceIdAndPackageName = deviceIdAndPackageName.toRemote(),
            message = FloconOutgoingMessageDataModel(
                plugin = Protocol.ToDevice.Network.Plugin,
                method = Protocol.ToDevice.Network.Method.SetupMocks,
                body = Json.Default.encodeToString(
                    listToRemote(mocks)
                ),
            ),
        )
    }
}

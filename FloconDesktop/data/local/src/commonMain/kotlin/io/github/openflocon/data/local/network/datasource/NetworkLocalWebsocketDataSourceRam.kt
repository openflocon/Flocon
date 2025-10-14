package io.github.openflocon.data.local.network.datasource

import io.github.openflocon.data.core.network.datasource.NetworkLocalWebsocketDataSource
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.NetworkWebsocketId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class NetworkLocalWebsocketDataSourceRam : NetworkLocalWebsocketDataSource {

    private val websocketsIds =
        MutableStateFlow<Map<DeviceIdAndPackageNameDomainModel, List<NetworkWebsocketId>>>(emptyMap())

    override suspend fun registerWebsocketClients(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        ids: List<NetworkWebsocketId>
    ) {
        websocketsIds.update { it + (deviceIdAndPackageName to ids) }
    }

    override suspend fun observeWebsocketClients(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<NetworkWebsocketId>> {
       return websocketsIds.map { it.get(deviceIdAndPackageName) ?: emptyList() }.map { it.distinct() }
    }

}

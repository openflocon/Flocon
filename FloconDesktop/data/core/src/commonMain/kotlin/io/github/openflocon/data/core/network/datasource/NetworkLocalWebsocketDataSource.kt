package io.github.openflocon.data.core.network.datasource

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.NetworkWebsocketId
import kotlinx.coroutines.flow.Flow

interface NetworkLocalWebsocketDataSource {
    suspend fun registerWebsocketClients(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        ids: List<NetworkWebsocketId>,
    )

    suspend fun observeWebsocketClients(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ) : Flow<List<NetworkWebsocketId>>
}

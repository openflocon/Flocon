package com.flocon.data.remote.analytics.datasource

import com.flocon.data.remote.Protocol
import com.flocon.data.remote.models.FloconOutgoingMessageDataModel
import com.flocon.data.remote.models.toRemote
import com.flocon.data.remote.server.Server
import io.github.openflocon.data.core.analytics.datasource.AnalyticsRemoteDataSource
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.serialization.json.Json

class AnalyticsRemoteDataSourceImpl(
    private val server: Server,
) : AnalyticsRemoteDataSource {
    override suspend fun clearReceivedItem(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, items: List<String>) {
        server.sendMessageToClient(
            deviceIdAndPackageName = deviceIdAndPackageName.toRemote(),
            message = FloconOutgoingMessageDataModel(
                plugin = Protocol.ToDevice.Analytics.Plugin,
                method = Protocol.ToDevice.Analytics.Method.ClearItems,
                body = Json.Default.encodeToString(items),
            )
        )
    }
}

package io.github.openflocon.flocondesktop.features.analytics.data.datasource

import com.flocon.data.remote.Protocol
import com.flocon.data.remote.models.FloconDeviceIdAndPackageNameDataModel
import com.flocon.data.remote.models.FloconOutgoingMessageDataModel
import com.flocon.data.remote.server.Server
import kotlinx.serialization.json.Json

class RemoteAnalyticsDataSource(
    private val server: Server,
) {
    // TODO Interface
    suspend fun clearReceivedItem(deviceIdAndPackageName: FloconDeviceIdAndPackageNameDataModel, items: List<String>) {
        server.sendMessageToClient(
            deviceIdAndPackageName = deviceIdAndPackageName,
            message = FloconOutgoingMessageDataModel(
                plugin = Protocol.ToDevice.Analytics.Plugin,
                method = Protocol.ToDevice.Analytics.Method.ClearItems,
                body = Json.encodeToString(items),
            ),
        )
    }
}

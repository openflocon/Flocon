package io.github.openflocon.flocondesktop.features.analytics.data.datasource

import io.github.openflocon.flocondesktop.FloconDeviceIdAndPackageName
import io.github.openflocon.flocondesktop.FloconOutgoingMessageDataModel
import io.github.openflocon.flocondesktop.Protocol
import io.github.openflocon.flocondesktop.Server
import kotlinx.serialization.json.Json

class RemoteAnalyticsDataSource(
    private val server: Server,
) {
    // TODO Interface
    suspend fun clearReceivedItem(deviceIdAndPackageName: FloconDeviceIdAndPackageName, items: List<String>) {
        server.sendMessageToClient(
            deviceIdAndPackageName = deviceIdAndPackageName,
            message = FloconOutgoingMessageDataModel(
                plugin = Protocol.ToDevice.Analytics.Plugin,
                method = Protocol.ToDevice.Analytics.Method.ClearItems,
                body = Json.encodeToString(items),
            )
        )
    }
}

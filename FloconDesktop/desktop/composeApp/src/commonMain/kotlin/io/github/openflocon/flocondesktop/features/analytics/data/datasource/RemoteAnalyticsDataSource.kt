package io.github.openflocon.flocondesktop.features.analytics.data.datasource

import io.github.openflocon.flocondesktop.FloconOutgoingMessageDataModel
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocondesktop.Server
import kotlinx.serialization.json.Json

class RemoteAnalyticsDataSource(
    private val server: Server,
) {
    suspend fun clearReceivedItem(deviceId: String, items: List<String>) {
        server.sendMessageToClient(
            deviceId = deviceId,
            message =
            FloconOutgoingMessageDataModel(
                plugin = Protocol.ToDevice.Analytics.Plugin,
                method = Protocol.ToDevice.Analytics.Method.ClearItems,
                body = Json.encodeToString(items),
            ),
        )
    }
}

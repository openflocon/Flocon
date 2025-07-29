package com.florent37.flocondesktop.features.analytics.data.datasource

import com.florent37.flocondesktop.FloconOutgoingMessageDataModel
import com.florent37.flocondesktop.Protocol
import com.florent37.flocondesktop.Server
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

package io.github.openflocon.flocondesktop.features.table.data.datasource

import com.florent37.flocondesktop.FloconOutgoingMessageDataModel
import com.florent37.flocondesktop.Protocol
import com.florent37.flocondesktop.Server
import kotlinx.serialization.json.Json

class RemoteTableDataSource(
    private val server: Server,
) {
    suspend fun clearReceivedItem(deviceId: String, items: List<String>) {
        server.sendMessageToClient(
            deviceId = deviceId,
            message =
            FloconOutgoingMessageDataModel(
                plugin = Protocol.ToDevice.Table.Plugin,
                method = Protocol.ToDevice.Table.Method.ClearItems,
                body = Json.encodeToString(items),
            ),
        )
    }
}

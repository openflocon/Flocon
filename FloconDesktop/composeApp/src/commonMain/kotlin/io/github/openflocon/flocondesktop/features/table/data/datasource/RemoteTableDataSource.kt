package io.github.openflocon.flocondesktop.features.table.data.datasource

import io.github.openflocon.flocondesktop.FloconOutgoingMessageDataModel
import io.github.openflocon.flocondesktop.Protocol
import io.github.openflocon.flocondesktop.Server
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageName
import io.github.openflocon.flocondesktop.messages.domain.model.toFlocon
import kotlinx.serialization.json.Json

class RemoteTableDataSource(
    private val server: Server,
) {
    suspend fun clearReceivedItem(deviceIdAndPackageName: DeviceIdAndPackageName, items: List<String>) {
        server.sendMessageToClient(
            deviceIdAndPackageName = deviceIdAndPackageName.toFlocon(),
            message =
                FloconOutgoingMessageDataModel(
                    plugin = Protocol.ToDevice.Table.Plugin,
                    method = Protocol.ToDevice.Table.Method.ClearItems,
                    body = Json.encodeToString(items),
                ),
        )
    }
}

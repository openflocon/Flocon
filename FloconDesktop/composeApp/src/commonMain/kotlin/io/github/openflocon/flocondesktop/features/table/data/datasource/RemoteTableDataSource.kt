package io.github.openflocon.flocondesktop.features.table.data.datasource

import com.flocon.data.remote.Protocol
import com.flocon.data.remote.models.FloconOutgoingMessageDataModel
import com.flocon.data.remote.models.toRemote
import com.flocon.data.remote.server.Server
import io.github.openflocon.domain.models.DeviceIdAndPackageNameDomainModel
import kotlinx.serialization.json.Json

class RemoteTableDataSource(
    private val server: Server,
) {
    suspend fun clearReceivedItem(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, items: List<String>) {
        server.sendMessageToClient(
            deviceIdAndPackageName = deviceIdAndPackageName.toRemote(),
            message =
                FloconOutgoingMessageDataModel(
                    plugin = Protocol.ToDevice.Table.Plugin,
                    method = Protocol.ToDevice.Table.Method.ClearItems,
                    body = Json.encodeToString(items),
                ),
        )
    }
}

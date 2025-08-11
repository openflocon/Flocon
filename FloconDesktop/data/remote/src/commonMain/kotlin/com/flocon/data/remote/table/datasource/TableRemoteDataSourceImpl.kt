package com.flocon.data.remote.table.datasource

import com.flocon.data.remote.Protocol
import com.flocon.data.remote.models.FloconOutgoingMessageDataModel
import com.flocon.data.remote.models.toRemote
import com.flocon.data.remote.server.Server
import io.github.openflocon.data.core.table.datasource.TableRemoteDataSource
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.serialization.json.Json

class TableRemoteDataSourceImpl(
    private val server: Server,
) : TableRemoteDataSource {
    override suspend fun clearReceivedItem(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, items: List<String>) {
        server.sendMessageToClient(
            deviceIdAndPackageName = deviceIdAndPackageName.toRemote(),
            message =
                FloconOutgoingMessageDataModel(
                    plugin = Protocol.ToDevice.Table.Plugin,
                    method = Protocol.ToDevice.Table.Method.ClearItems,
                    body = Json.Default.encodeToString(items),
                ),
        )
    }
}

package com.flocon.data.remote.analytics.datasource

import com.flocon.data.remote.analytics.mapper.toDomain
import com.flocon.data.remote.analytics.model.AnalyticsItemDataModel
import com.flocon.data.remote.models.FloconOutgoingMessageDataModel
import com.flocon.data.remote.models.toRemote
import com.flocon.data.remote.server.Server
import io.github.openflocon.data.core.analytics.datasource.AnalyticsRemoteDataSource
import io.github.openflocon.domain.Protocol
import io.github.openflocon.domain.analytics.models.AnalyticsItemDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import kotlinx.serialization.json.Json

class AnalyticsRemoteDataSourceImpl(
    private val server: Server,
    private val json: Json,
) : AnalyticsRemoteDataSource {

    override suspend fun clearReceivedItem(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, items: List<String>) {
        server.sendMessageToClient(
            deviceIdAndPackageName = deviceIdAndPackageName.toRemote(),
            message = FloconOutgoingMessageDataModel(
                plugin = Protocol.ToDevice.Analytics.Plugin,
                method = Protocol.ToDevice.Analytics.Method.ClearItems,
                body = Json.Default.encodeToString(items),
            ),
        )
    }

    override fun getItems(message: FloconIncomingMessageDomainModel): List<AnalyticsItemDomainModel> = decodeAddItems(message).takeIf { it.isNotEmpty() }
        ?.let { list -> list.map { toDomain(it) } }
        ?.takeIf { it.isNotEmpty() }
        .orEmpty()

    private fun decodeAddItems(message: FloconIncomingMessageDomainModel): List<AnalyticsItemDataModel> = try {
        json.decodeFromString<List<AnalyticsItemDataModel>>(message.body)
    } catch (t: Throwable) {
        t.printStackTrace()
        emptyList()
    }
}

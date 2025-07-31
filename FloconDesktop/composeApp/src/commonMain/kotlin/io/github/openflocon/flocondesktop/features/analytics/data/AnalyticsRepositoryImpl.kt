package io.github.openflocon.flocondesktop.features.analytics.data

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.FloconIncomingMessageDataModel
import com.florent37.flocondesktop.Protocol
import com.florent37.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import com.florent37.flocondesktop.features.analytics.data.datasource.RemoteAnalyticsDataSource
import com.florent37.flocondesktop.features.analytics.data.datasource.device.DeviceAnalyticsDataSource
import com.florent37.flocondesktop.features.analytics.data.datasource.local.AnalyticsLocalDataSource
import com.florent37.flocondesktop.features.analytics.data.mapper.toDomain
import com.florent37.flocondesktop.features.analytics.data.model.AnalyticsItemDataModel
import com.florent37.flocondesktop.features.analytics.domain.model.AnalyticsIdentifierDomainModel
import com.florent37.flocondesktop.features.analytics.domain.model.AnalyticsItemDomainModel
import com.florent37.flocondesktop.features.analytics.domain.model.AnalyticsTableId
import com.florent37.flocondesktop.features.analytics.domain.repository.AnalyticsRepository
import com.florent37.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class AnalyticsRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val analyticsLocalDataSource: AnalyticsLocalDataSource,
    private val deviceAnalyticsDataSource: DeviceAnalyticsDataSource,
    private val remoteAnalyticsDataSource: RemoteAnalyticsDataSource,
) : AnalyticsRepository,
    MessagesReceiverRepository {

    // maybe inject
    private val analyticsParser =
        Json {
            ignoreUnknownKeys = true
        }

    override val pluginName = listOf(Protocol.FromDevice.Analytics.Plugin)

    override suspend fun onMessageReceived(
        deviceId: String,
        message: FloconIncomingMessageDataModel,
    ) {
        withContext(dispatcherProvider.data) {
            when (message.method) {
                Protocol.FromDevice.Analytics.Method.AddItems -> {
                    decodeAddItems(message)
                        .takeIf { it.isNotEmpty() }
                        ?.let { list -> list.map { toDomain(it) } }
                        ?.takeIf { it.isNotEmpty() }
                        ?.let { analytics ->
                            analyticsLocalDataSource.insert(
                                deviceId = deviceId,
                                items = analytics,
                            )
                            remoteAnalyticsDataSource.clearReceivedItem(
                                deviceId = deviceId,
                                items = analytics.map { it.itemId },
                            )
                        }
                }
            }
        }
    }

    private fun decodeAddItems(message: FloconIncomingMessageDataModel): List<AnalyticsItemDataModel> = try {
        analyticsParser.decodeFromString<List<AnalyticsItemDataModel>>(message.body)
    } catch (t: Throwable) {
        t.printStackTrace()
        emptyList()
    }

    override fun observeAnalytics(
        deviceId: DeviceId,
        analyticsTableId: AnalyticsTableId,
    ): Flow<List<AnalyticsItemDomainModel>> = analyticsLocalDataSource.observe(deviceId = deviceId, analyticsTableId = analyticsTableId)
        .flowOn(dispatcherProvider.data)

    override suspend fun deleteAnalytics(
        deviceId: DeviceId,
        analyticsId: AnalyticsIdentifierDomainModel,
    ) {
        withContext(dispatcherProvider.data) {
            analyticsLocalDataSource.delete(deviceId = deviceId, analyticsId = analyticsId)
        }
    }

    override suspend fun selectDeviceAnalytics(
        deviceId: DeviceId,
        analyticsTableId: AnalyticsTableId,
    ) {
        deviceAnalyticsDataSource.selectDeviceAnalytics(
            deviceId = deviceId,
            analyticsTableId = analyticsTableId,
            deviceAnalytics = analyticsLocalDataSource.getDeviceAnalytics(deviceId = deviceId),
        )
    }

    override fun observeSelectedDeviceAnalytics(deviceId: DeviceId): Flow<AnalyticsIdentifierDomainModel?> = deviceAnalyticsDataSource.observeSelectedDeviceAnalytics(deviceId = deviceId)
        .flowOn(dispatcherProvider.data)

    override fun observeDeviceAnalytics(deviceId: DeviceId): Flow<List<AnalyticsIdentifierDomainModel>> = analyticsLocalDataSource.observeDeviceAnalytics(deviceId = deviceId)
        .flowOn(dispatcherProvider.data)
}

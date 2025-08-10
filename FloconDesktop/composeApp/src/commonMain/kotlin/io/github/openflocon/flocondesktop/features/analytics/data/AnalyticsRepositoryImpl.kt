package io.github.openflocon.flocondesktop.features.analytics.data

import io.github.openflocon.flocondesktop.FloconDeviceIdAndPackageName
import io.github.openflocon.flocondesktop.FloconIncomingMessageDataModel
import io.github.openflocon.flocondesktop.Protocol
import io.github.openflocon.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import io.github.openflocon.flocondesktop.features.analytics.data.datasource.RemoteAnalyticsDataSource
import io.github.openflocon.flocondesktop.features.analytics.data.datasource.device.DeviceAnalyticsDataSource
import io.github.openflocon.flocondesktop.features.analytics.data.datasource.local.AnalyticsLocalDataSource
import io.github.openflocon.flocondesktop.features.analytics.data.mapper.toDomain
import io.github.openflocon.flocondesktop.features.analytics.data.model.AnalyticsItemDataModel
import io.github.openflocon.flocondesktop.features.analytics.domain.model.AnalyticsIdentifierDomainModel
import io.github.openflocon.flocondesktop.features.analytics.domain.model.AnalyticsItemDomainModel
import io.github.openflocon.flocondesktop.features.analytics.domain.model.AnalyticsTableId
import io.github.openflocon.flocondesktop.features.analytics.domain.repository.AnalyticsRepository
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
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
    private val analyticsParser = Json {
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
                                deviceIdAndPackageName = DeviceIdAndPackageNameDomainModel(
                                    deviceId = deviceId,
                                    packageName = message.appPackageName
                                ),
                                items = analytics,
                            )
                            remoteAnalyticsDataSource.clearReceivedItem(
                                deviceIdAndPackageName = FloconDeviceIdAndPackageName(
                                    deviceId = deviceId,
                                    packageName = message.appPackageName
                                ),
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
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        analyticsTableId: AnalyticsTableId
    ): Flow<List<AnalyticsItemDomainModel>> = analyticsLocalDataSource.observe(
        deviceIdAndPackageName = deviceIdAndPackageName,
        analyticsTableId = analyticsTableId
    )
        .flowOn(dispatcherProvider.data)

    override suspend fun deleteAnalytics(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        analyticsId: AnalyticsIdentifierDomainModel
    ) {
        withContext(dispatcherProvider.data) {
            analyticsLocalDataSource.delete(
                deviceIdAndPackageName = deviceIdAndPackageName,
                analyticsId = analyticsId
            )
        }
    }

    override suspend fun selectDeviceAnalytics(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, analyticsTableId: AnalyticsTableId) {
        deviceAnalyticsDataSource.selectDeviceAnalytics(
            deviceIdAndPackageName = deviceIdAndPackageName,
            analyticsTableId = analyticsTableId,
            deviceAnalytics = analyticsLocalDataSource.getDeviceAnalytics(deviceIdAndPackageName = deviceIdAndPackageName)
        )
    }

    override fun observeSelectedDeviceAnalytics(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<AnalyticsIdentifierDomainModel?> =
        deviceAnalyticsDataSource.observeSelectedDeviceAnalytics(deviceIdAndPackageName)
            .flowOn(dispatcherProvider.data)

    override fun observeDeviceAnalytics(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<AnalyticsIdentifierDomainModel>> =
        analyticsLocalDataSource.observeDeviceAnalytics(deviceIdAndPackageName)
            .flowOn(dispatcherProvider.data)
}

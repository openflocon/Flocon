package io.github.openflocon.data.core.analytics.repository

import io.github.openflocon.data.core.analytics.datasource.AnalyticsLocalDataSource
import io.github.openflocon.data.core.analytics.datasource.AnalyticsRemoteDataSource
import io.github.openflocon.data.core.analytics.datasource.DeviceAnalyticsDataSource
import io.github.openflocon.domain.Protocol
import io.github.openflocon.domain.analytics.models.AnalyticsIdentifierDomainModel
import io.github.openflocon.domain.analytics.models.AnalyticsItemDomainModel
import io.github.openflocon.domain.analytics.models.AnalyticsTableId
import io.github.openflocon.domain.analytics.repository.AnalyticsRepository
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.messages.repository.MessagesReceiverRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class AnalyticsRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val analyticsLocalDataSource: AnalyticsLocalDataSource,
    private val deviceAnalyticsDataSource: DeviceAnalyticsDataSource,
    private val remoteAnalyticsDataSource: AnalyticsRemoteDataSource,
) : AnalyticsRepository,
    MessagesReceiverRepository {

    override val pluginName = listOf(Protocol.FromDevice.Analytics.Plugin)

    override suspend fun onMessageReceived(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        message: FloconIncomingMessageDomainModel
    ) {
        withContext(dispatcherProvider.data) {
            when (message.method) {
                Protocol.FromDevice.Analytics.Method.AddItems -> {
                    val items = remoteAnalyticsDataSource.getItems(message)

                    analyticsLocalDataSource.insert(
                        deviceIdAndPackageName = deviceIdAndPackageName,
                        items = items,
                    )
                }
            }
        }
    }

    override suspend fun onDeviceConnected(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        isNewDevice: Boolean,
    ) {
        // no op
    }

    override fun observeAnalytics(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        analyticsTableId: AnalyticsTableId,
        filter: String?,
    ): Flow<List<AnalyticsItemDomainModel>> = analyticsLocalDataSource.observe(
        deviceIdAndPackageName = deviceIdAndPackageName,
        analyticsTableId = analyticsTableId,
        filter = filter,
    ).flowOn(dispatcherProvider.data)

    override suspend fun getAnalytics(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        analyticsTableId: AnalyticsTableId,
        filter: String?,
    ): List<AnalyticsItemDomainModel> = withContext(dispatcherProvider.data) {
        analyticsLocalDataSource.getItems(
            deviceIdAndPackageName = deviceIdAndPackageName,
            analyticsTableId = analyticsTableId,
            filter = filter,
        )
    }

    override fun observeAnalyticsById(
        id: String,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel
    ): Flow<AnalyticsItemDomainModel?> {
        return analyticsLocalDataSource.observeById(
            id = id,
            deviceIdAndPackageName = deviceIdAndPackageName,
        ).flowOn(dispatcherProvider.data)
    }

    override suspend fun deleteAnalytics(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        analyticsId: AnalyticsIdentifierDomainModel,
    ) {
        withContext(dispatcherProvider.data) {
            analyticsLocalDataSource.delete(
                deviceIdAndPackageName = deviceIdAndPackageName,
                analyticsId = analyticsId,
            )
        }
    }

    override suspend fun selectDeviceAnalytics(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        analyticsTableId: AnalyticsTableId
    ) {
        withContext(dispatcherProvider.data) {
        deviceAnalyticsDataSource.selectDeviceAnalytics(
            deviceIdAndPackageName = deviceIdAndPackageName,
            analyticsTableId = analyticsTableId,
            deviceAnalytics = analyticsLocalDataSource.getDeviceAnalytics(deviceIdAndPackageName = deviceIdAndPackageName),
        )
            }
    }

    override fun observeSelectedDeviceAnalytics(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<AnalyticsIdentifierDomainModel?> =
        deviceAnalyticsDataSource.observeSelectedDeviceAnalytics(deviceIdAndPackageName)
            .flowOn(dispatcherProvider.data)

    override fun observeDeviceAnalytics(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<AnalyticsIdentifierDomainModel>> =
        analyticsLocalDataSource.observeDeviceAnalytics(deviceIdAndPackageName)
            .flowOn(dispatcherProvider.data)

    override suspend fun deleteAnalyticsItem(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        analyticsItemId: String
    ) {
        withContext(dispatcherProvider.data) {
            analyticsLocalDataSource.deleteAnalyticsItem(
                deviceIdAndPackageName = deviceIdAndPackageName,
                analyticsItemId = analyticsItemId,
            )
        }
    }

    override suspend fun deleteBefore(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        analyticsItemId: String
    ) {
        withContext(dispatcherProvider.data) {
            analyticsLocalDataSource.deleteBefore(
                deviceIdAndPackageName = deviceIdAndPackageName,
                analyticsItemId = analyticsItemId,
            )
        }
    }

    override suspend fun deleteOldSessions(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) {
        withContext(dispatcherProvider.data) {
            analyticsLocalDataSource.deleteRequestOnDifferentSession(deviceIdAndPackageName = deviceIdAndPackageName)
        }
    }
}

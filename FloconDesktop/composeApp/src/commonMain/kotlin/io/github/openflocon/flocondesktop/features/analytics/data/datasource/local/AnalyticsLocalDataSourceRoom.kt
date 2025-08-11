package io.github.openflocon.flocondesktop.features.analytics.data.datasource.local

import io.github.openflocon.data.core.analytics.datasource.AnalyticsLocalDataSource
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.flocondesktop.features.analytics.data.datasource.local.mapper.toAnalyticsDomain
import io.github.openflocon.flocondesktop.features.analytics.data.datasource.local.mapper.toEntity
import io.github.openflocon.domain.analytics.models.AnalyticsIdentifierDomainModel
import io.github.openflocon.domain.analytics.models.AnalyticsItemDomainModel
import io.github.openflocon.domain.analytics.models.AnalyticsTableId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AnalyticsLocalDataSourceRoom(
    private val analyticsDao: FloconAnalyticsDao,
    private val dispatcherProvider: DispatcherProvider,
) : AnalyticsLocalDataSource {

    override suspend fun insert(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, items: List<AnalyticsItemDomainModel>) {
        withContext(dispatcherProvider.data) {
            analyticsDao.insertAnalyticsItems(
                items.map { item ->
                    item.toEntity(deviceIdAndPackageName)
                },
            )
        }
    }

    override fun observe(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, analyticsTableId: AnalyticsTableId): Flow<List<AnalyticsItemDomainModel>> = analyticsDao.observeAnalyticsItems(
        deviceId = deviceIdAndPackageName.deviceId,
        packageName = deviceIdAndPackageName.packageName,
        analyticsTableId = analyticsTableId,
    )
        .map { it.map(::toAnalyticsDomain) }
        .flowOn(dispatcherProvider.data)

    override fun observeDeviceAnalytics(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<AnalyticsIdentifierDomainModel>> = analyticsDao.observeAnalyticsTableIdsForDevice(
        deviceId = deviceIdAndPackageName.deviceId,
        packageName = deviceIdAndPackageName.packageName,
    )
        .map { list ->
            list.map {
                AnalyticsIdentifierDomainModel(
                    id = it,
                    name = it,
                )
            }
        }

    override suspend fun delete(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, analyticsId: AnalyticsIdentifierDomainModel) {
        analyticsDao.deleteAnalyticsContent(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
            analyticsTableId = analyticsId.id,
        )
    }

    override suspend fun getDeviceAnalytics(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): List<AnalyticsIdentifierDomainModel> = analyticsDao.getAnalyticsForDevice(
        deviceId = deviceIdAndPackageName.deviceId,
        packageName = deviceIdAndPackageName.packageName,
    )
        .map {
            AnalyticsIdentifierDomainModel(
                id = it,
                name = it,
            )
        }
}

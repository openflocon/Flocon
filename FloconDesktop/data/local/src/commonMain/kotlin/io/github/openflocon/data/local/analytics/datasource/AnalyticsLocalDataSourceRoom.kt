package io.github.openflocon.data.local.analytics.datasource

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import io.github.openflocon.data.core.analytics.datasource.AnalyticsLocalDataSource
import io.github.openflocon.data.local.analytics.dao.FloconAnalyticsDao
import io.github.openflocon.data.local.analytics.mapper.toAnalyticsDomain
import io.github.openflocon.data.local.analytics.mapper.toEntity
import io.github.openflocon.domain.analytics.models.AnalyticsIdentifierDomainModel
import io.github.openflocon.domain.analytics.models.AnalyticsItemDomainModel
import io.github.openflocon.domain.analytics.models.AnalyticsTableId
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
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

    override fun observe(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        analyticsTableId: AnalyticsTableId,
        filter: String?,
    ): Flow<PagingData<AnalyticsItemDomainModel>> = Pager(
        config = PagingConfig(
            pageSize = 20,
        ),
        pagingSourceFactory = {
            analyticsDao.observeAnalyticsItems(
                deviceId = deviceIdAndPackageName.deviceId,
                packageName = deviceIdAndPackageName.packageName,
                analyticsTableId = analyticsTableId,
                filter = filter,
            )
        }
    ).flow
        .map { pagingData ->
            pagingData.map { entity -> entity.toAnalyticsDomain() }
        }

    override suspend fun getItems(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        analyticsTableId: AnalyticsTableId,
        filter: String?,
    ): List<AnalyticsItemDomainModel> = analyticsDao.getAnalyticsItems(
        deviceId = deviceIdAndPackageName.deviceId,
        packageName = deviceIdAndPackageName.packageName,
        analyticsTableId = analyticsTableId,
        filter = filter,
    ).let { it.map { it.toAnalyticsDomain() } }

    override fun observeById(
        id: String,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel
    ): Flow<AnalyticsItemDomainModel?> = analyticsDao.observeAnalyticsItemById(
        deviceId = deviceIdAndPackageName.deviceId,
        packageName = deviceIdAndPackageName.packageName,
        analyticsItemId = id,
    ).map {
        it?.toAnalyticsDomain()
    }.distinctUntilChanged()

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
        }.distinctUntilChanged()

    override suspend fun delete(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, analyticsId: AnalyticsIdentifierDomainModel) {
        analyticsDao.deleteAnalyticsContent(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
            analyticsTableId = analyticsId.id,
        )
    }

    override suspend fun deleteAnalyticsItem(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        analyticsItemId: String
    ) {
        analyticsDao.deleteAnalyticsItem(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
            analyticsItemId = analyticsItemId,
        )
    }

    override suspend fun deleteBefore(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        analyticsItemId: String
    ) {
        analyticsDao.deleteBefore(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
            analyticsItemId = analyticsItemId,
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

    override suspend fun deleteRequestOnDifferentSession(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) {
        analyticsDao.deleteRequestOnDifferentSession(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
            appInstance = deviceIdAndPackageName.appInstance
        )
    }
}

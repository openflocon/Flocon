package io.github.openflocon.flocondesktop.features.analytics.data.datasource.local

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import io.github.openflocon.flocondesktop.features.analytics.data.datasource.local.mapper.toAnalyticsDomain
import io.github.openflocon.flocondesktop.features.analytics.data.datasource.local.mapper.toEntity
import io.github.openflocon.flocondesktop.features.analytics.domain.model.AnalyticsIdentifierDomainModel
import io.github.openflocon.flocondesktop.features.analytics.domain.model.AnalyticsItemDomainModel
import io.github.openflocon.flocondesktop.features.analytics.domain.model.AnalyticsTableId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AnalyticsLocalDataSourceRoom(
    private val analyticsDao: FloconAnalyticsDao,
    private val dispatcherProvider: DispatcherProvider,
) : AnalyticsLocalDataSource {

    override suspend fun insert(
        deviceId: DeviceId,
        items: List<AnalyticsItemDomainModel>,
    ) {
        withContext(dispatcherProvider.data) {
            analyticsDao.insertAnalyticsItems(
                items.map { item ->
                    item.toEntity(
                        deviceId = deviceId,
                    )
                },
            )
        }
    }

    override fun observe(
        deviceId: DeviceId,
        analyticsTableId: AnalyticsTableId,
    ): Flow<List<AnalyticsItemDomainModel>> = analyticsDao.observeAnalyticsItems(
        deviceId = deviceId,
        analyticsTableId = analyticsTableId,
    ).map {
        it.map {
            toAnalyticsDomain(it)
        }
    }.flowOn(dispatcherProvider.data)

    override fun observeDeviceAnalytics(deviceId: DeviceId): Flow<List<AnalyticsIdentifierDomainModel>> = analyticsDao.observeAnalyticsTableIdsForDevice(deviceId).map { list ->
        list.map {
            AnalyticsIdentifierDomainModel(
                id = it,
                name = it,
            )
        }
    }

    override suspend fun delete(deviceId: DeviceId, analyticsId: AnalyticsIdentifierDomainModel) {
        analyticsDao.deleteAnalyticsContent(
            deviceId = deviceId,
            analyticsTableId = analyticsId.id,
        )
    }

    override suspend fun getDeviceAnalytics(deviceId: DeviceId): List<AnalyticsIdentifierDomainModel> = analyticsDao.getAnalyticsForDevice(deviceId).map {
        AnalyticsIdentifierDomainModel(
            id = it,
            name = it,
        )
    }
}

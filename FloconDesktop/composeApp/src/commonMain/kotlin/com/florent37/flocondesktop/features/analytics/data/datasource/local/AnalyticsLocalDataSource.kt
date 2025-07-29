package com.florent37.flocondesktop.features.analytics.data.datasource.local

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.features.analytics.domain.model.AnalyticsIdentifierDomainModel
import com.florent37.flocondesktop.features.analytics.domain.model.AnalyticsItemDomainModel
import com.florent37.flocondesktop.features.analytics.domain.model.AnalyticsTableId
import kotlinx.coroutines.flow.Flow

interface AnalyticsLocalDataSource {
    suspend fun insert(deviceId: DeviceId, items: List<AnalyticsItemDomainModel>)
    fun observe(deviceId: DeviceId, analyticsTableId: AnalyticsTableId): Flow<List<AnalyticsItemDomainModel>>
    fun observeDeviceAnalytics(deviceId: DeviceId): Flow<List<AnalyticsIdentifierDomainModel>>
    suspend fun getDeviceAnalytics(deviceId: DeviceId): List<AnalyticsIdentifierDomainModel>

    suspend fun delete(
        deviceId: DeviceId,
        analyticsId: AnalyticsIdentifierDomainModel,
    )
}

package com.florent37.flocondesktop.features.analytics.domain.repository

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.features.analytics.domain.model.AnalyticsIdentifierDomainModel
import com.florent37.flocondesktop.features.analytics.domain.model.AnalyticsItemDomainModel
import com.florent37.flocondesktop.features.analytics.domain.model.AnalyticsTableId
import kotlinx.coroutines.flow.Flow

interface AnalyticsRepository {
    fun observeAnalytics(deviceId: DeviceId, analyticsTableId: AnalyticsTableId): Flow<List<AnalyticsItemDomainModel>>
    suspend fun deleteAnalytics(deviceId: DeviceId, analyticsId: AnalyticsIdentifierDomainModel)

    suspend fun selectDeviceAnalytics(deviceId: DeviceId, analyticsTableId: AnalyticsTableId)
    fun observeSelectedDeviceAnalytics(deviceId: DeviceId): Flow<AnalyticsIdentifierDomainModel?>
    fun observeDeviceAnalytics(deviceId: DeviceId): Flow<List<AnalyticsIdentifierDomainModel>>
}

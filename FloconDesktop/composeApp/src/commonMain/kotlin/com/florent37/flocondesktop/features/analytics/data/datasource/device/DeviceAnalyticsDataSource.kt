package com.florent37.flocondesktop.features.analytics.data.datasource.device

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.features.analytics.domain.model.AnalyticsIdentifierDomainModel
import com.florent37.flocondesktop.features.analytics.domain.model.AnalyticsTableId
import kotlinx.coroutines.flow.Flow

interface DeviceAnalyticsDataSource {
    fun observeSelectedDeviceAnalytics(deviceId: DeviceId): Flow<AnalyticsIdentifierDomainModel?>
    fun selectDeviceAnalytics(
        deviceAnalytics: List<AnalyticsIdentifierDomainModel>,
        deviceId: DeviceId,
        analyticsTableId: AnalyticsTableId,
    )
}

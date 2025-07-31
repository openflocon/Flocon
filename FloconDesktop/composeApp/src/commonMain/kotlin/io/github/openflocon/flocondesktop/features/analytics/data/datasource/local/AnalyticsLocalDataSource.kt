package io.github.openflocon.flocondesktop.features.analytics.data.datasource.local

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.features.analytics.domain.model.AnalyticsIdentifierDomainModel
import io.github.openflocon.flocondesktop.features.analytics.domain.model.AnalyticsItemDomainModel
import io.github.openflocon.flocondesktop.features.analytics.domain.model.AnalyticsTableId
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

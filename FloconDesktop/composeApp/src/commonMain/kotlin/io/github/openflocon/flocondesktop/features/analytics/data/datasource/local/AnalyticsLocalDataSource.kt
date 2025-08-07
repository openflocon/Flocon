package io.github.openflocon.flocondesktop.features.analytics.data.datasource.local

import io.github.openflocon.flocondesktop.features.analytics.domain.model.AnalyticsIdentifierDomainModel
import io.github.openflocon.flocondesktop.features.analytics.domain.model.AnalyticsItemDomainModel
import io.github.openflocon.flocondesktop.features.analytics.domain.model.AnalyticsTableId
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageName
import kotlinx.coroutines.flow.Flow

interface AnalyticsLocalDataSource {
    suspend fun insert(deviceIdAndPackageName: DeviceIdAndPackageName, items: List<AnalyticsItemDomainModel>)
    fun observe(deviceIdAndPackageName: DeviceIdAndPackageName, analyticsTableId: AnalyticsTableId): Flow<List<AnalyticsItemDomainModel>>
    fun observeDeviceAnalytics(deviceIdAndPackageName: DeviceIdAndPackageName): Flow<List<AnalyticsIdentifierDomainModel>>
    suspend fun getDeviceAnalytics(deviceIdAndPackageName: DeviceIdAndPackageName): List<AnalyticsIdentifierDomainModel>

    suspend fun delete(
        deviceIdAndPackageName: DeviceIdAndPackageName,
        analyticsId: AnalyticsIdentifierDomainModel,
    )
}

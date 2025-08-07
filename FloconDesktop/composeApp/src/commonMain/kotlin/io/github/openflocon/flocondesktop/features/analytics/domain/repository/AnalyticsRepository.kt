package io.github.openflocon.flocondesktop.features.analytics.domain.repository

import io.github.openflocon.flocondesktop.features.analytics.domain.model.AnalyticsIdentifierDomainModel
import io.github.openflocon.flocondesktop.features.analytics.domain.model.AnalyticsItemDomainModel
import io.github.openflocon.flocondesktop.features.analytics.domain.model.AnalyticsTableId
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageName
import kotlinx.coroutines.flow.Flow

interface AnalyticsRepository {
    fun observeAnalytics(deviceIdAndPackageName: DeviceIdAndPackageName, analyticsTableId: AnalyticsTableId): Flow<List<AnalyticsItemDomainModel>>
    suspend fun deleteAnalytics(deviceIdAndPackageName: DeviceIdAndPackageName, analyticsId: AnalyticsIdentifierDomainModel)

    suspend fun selectDeviceAnalytics(deviceIdAndPackageName: DeviceIdAndPackageName, analyticsTableId: AnalyticsTableId)
    fun observeSelectedDeviceAnalytics(deviceIdAndPackageName: DeviceIdAndPackageName): Flow<AnalyticsIdentifierDomainModel?>
    fun observeDeviceAnalytics(deviceIdAndPackageName: DeviceIdAndPackageName): Flow<List<AnalyticsIdentifierDomainModel>>
}

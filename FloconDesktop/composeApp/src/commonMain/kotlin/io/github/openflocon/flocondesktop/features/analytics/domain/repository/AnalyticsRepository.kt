package io.github.openflocon.flocondesktop.features.analytics.domain.repository

import io.github.openflocon.flocondesktop.features.analytics.domain.model.AnalyticsIdentifierDomainModel
import io.github.openflocon.flocondesktop.features.analytics.domain.model.AnalyticsItemDomainModel
import io.github.openflocon.flocondesktop.features.analytics.domain.model.AnalyticsTableId
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface AnalyticsRepository {
    fun observeAnalytics(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, analyticsTableId: AnalyticsTableId): Flow<List<AnalyticsItemDomainModel>>
    suspend fun deleteAnalytics(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, analyticsId: AnalyticsIdentifierDomainModel)

    suspend fun selectDeviceAnalytics(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, analyticsTableId: AnalyticsTableId)
    fun observeSelectedDeviceAnalytics(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<AnalyticsIdentifierDomainModel?>
    fun observeDeviceAnalytics(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<AnalyticsIdentifierDomainModel>>
}

package io.github.openflocon.domain.analytics.repository

import io.github.openflocon.domain.analytics.models.AnalyticsIdentifierDomainModel
import io.github.openflocon.domain.analytics.models.AnalyticsItemDomainModel
import io.github.openflocon.domain.analytics.models.AnalyticsTableId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface AnalyticsRepository {
    fun observeAnalytics(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, analyticsTableId: AnalyticsTableId): Flow<List<AnalyticsItemDomainModel>>
    suspend fun deleteAnalytics(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, analyticsId: AnalyticsIdentifierDomainModel)

    suspend fun selectDeviceAnalytics(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, analyticsTableId: AnalyticsTableId)
    fun observeSelectedDeviceAnalytics(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<AnalyticsIdentifierDomainModel?>
    fun observeDeviceAnalytics(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<AnalyticsIdentifierDomainModel>>

    suspend fun deleteAnalyticsItem(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        analyticsItemId: String
    )

    suspend fun deleteBefore(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        analyticsItemId: String
    )

    suspend fun deleteOldSessions(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel)
}

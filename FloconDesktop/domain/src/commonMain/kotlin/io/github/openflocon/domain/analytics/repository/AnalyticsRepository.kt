package io.github.openflocon.domain.analytics.repository

import androidx.paging.PagingData
import io.github.openflocon.domain.analytics.models.AnalyticsEventCountDomainModel
import io.github.openflocon.domain.analytics.models.AnalyticsIdentifierDomainModel
import io.github.openflocon.domain.analytics.models.AnalyticsItemDomainModel
import io.github.openflocon.domain.analytics.models.AnalyticsSearchDomainModel
import io.github.openflocon.domain.analytics.models.AnalyticsTableId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface AnalyticsRepository {
    fun observeAnalytics(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        analyticsTableId: AnalyticsTableId,
        filter: String?,
    ): Flow<PagingData<AnalyticsItemDomainModel>>

    suspend fun getAnalytics(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        analyticsTableId: AnalyticsTableId,
        filter: String?,
    ): List<AnalyticsItemDomainModel>

    /**
     * Filtered, sorted and limited by the database
     */
    suspend fun searchAnalytics(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        search: AnalyticsSearchDomainModel,
        newestFirst: Boolean,
        limit: Int,
    ): List<AnalyticsItemDomainModel>

    /**
     * Number of events per event name, the most frequent first
     */
    suspend fun countAnalyticsByEventName(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        search: AnalyticsSearchDomainModel,
    ): List<AnalyticsEventCountDomainModel>

    suspend fun deleteAnalytics(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, analyticsId: AnalyticsIdentifierDomainModel)
    fun observeAnalyticsById(id: String, deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<AnalyticsItemDomainModel?>

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

package io.github.openflocon.data.core.analytics.datasource

import androidx.paging.PagingData
import io.github.openflocon.domain.analytics.models.AnalyticsIdentifierDomainModel
import io.github.openflocon.domain.analytics.models.AnalyticsItemDomainModel
import io.github.openflocon.domain.analytics.models.AnalyticsTableId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface AnalyticsLocalDataSource {
    suspend fun insert(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, items: List<AnalyticsItemDomainModel>)

    fun observe(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        analyticsTableId: AnalyticsTableId,
        filter: String?,
    ): Flow<PagingData<AnalyticsItemDomainModel>>

    suspend fun getItems(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        analyticsTableId: AnalyticsTableId,
        filter: String?,
    ): List<AnalyticsItemDomainModel>

    fun observeDeviceAnalytics(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<AnalyticsIdentifierDomainModel>>
    suspend fun getDeviceAnalytics(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): List<AnalyticsIdentifierDomainModel>
    fun observeById(id: String, deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) : Flow<AnalyticsItemDomainModel?>

    suspend fun delete(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        analyticsId: AnalyticsIdentifierDomainModel,
    )

    suspend fun deleteAnalyticsItem(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        analyticsItemId: String
    )

    suspend fun deleteBefore(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        analyticsItemId: String
    )

    suspend fun deleteRequestOnDifferentSession(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel)
}

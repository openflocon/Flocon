package io.github.openflocon.data.core.analytics.datasource

import io.github.openflocon.domain.analytics.models.AnalyticsIdentifierDomainModel
import io.github.openflocon.domain.analytics.models.AnalyticsItemDomainModel
import io.github.openflocon.domain.analytics.models.AnalyticsTableId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface AnalyticsLocalDataSource {
    suspend fun insert(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, items: List<AnalyticsItemDomainModel>)
    fun observe(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, analyticsTableId: AnalyticsTableId): Flow<List<AnalyticsItemDomainModel>>
    fun observeDeviceAnalytics(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<AnalyticsIdentifierDomainModel>>
    suspend fun getDeviceAnalytics(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): List<AnalyticsIdentifierDomainModel>

    suspend fun delete(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        analyticsId: AnalyticsIdentifierDomainModel,
    )
}

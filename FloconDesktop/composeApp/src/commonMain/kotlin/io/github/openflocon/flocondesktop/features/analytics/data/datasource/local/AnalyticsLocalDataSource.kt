package io.github.openflocon.flocondesktop.features.analytics.data.datasource.local

import io.github.openflocon.domain.models.AnalyticsIdentifierDomainModel
import io.github.openflocon.domain.models.AnalyticsItemDomainModel
import io.github.openflocon.domain.models.AnalyticsTableId
import io.github.openflocon.domain.models.DeviceIdAndPackageNameDomainModel
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

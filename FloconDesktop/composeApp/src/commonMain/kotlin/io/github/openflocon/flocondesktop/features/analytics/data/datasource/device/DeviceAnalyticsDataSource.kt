package io.github.openflocon.flocondesktop.features.analytics.data.datasource.device

import io.github.openflocon.domain.analytics.models.AnalyticsIdentifierDomainModel
import io.github.openflocon.domain.analytics.models.AnalyticsTableId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface DeviceAnalyticsDataSource {
    fun observeSelectedDeviceAnalytics(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<AnalyticsIdentifierDomainModel?>
    fun selectDeviceAnalytics(
        deviceAnalytics: List<AnalyticsIdentifierDomainModel>,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        analyticsTableId: AnalyticsTableId,
    )
}

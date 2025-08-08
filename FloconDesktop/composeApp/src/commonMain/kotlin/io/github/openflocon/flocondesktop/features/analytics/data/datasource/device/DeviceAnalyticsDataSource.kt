package io.github.openflocon.flocondesktop.features.analytics.data.datasource.device

import io.github.openflocon.flocondesktop.features.analytics.domain.model.AnalyticsIdentifierDomainModel
import io.github.openflocon.flocondesktop.features.analytics.domain.model.AnalyticsTableId
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface DeviceAnalyticsDataSource {
    fun observeSelectedDeviceAnalytics(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<AnalyticsIdentifierDomainModel?>
    fun selectDeviceAnalytics(
        deviceAnalytics: List<AnalyticsIdentifierDomainModel>,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        analyticsTableId: AnalyticsTableId,
    )
}

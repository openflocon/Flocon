package io.github.openflocon.flocondesktop.features.analytics.data.datasource.device

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.features.analytics.domain.model.AnalyticsIdentifierDomainModel
import io.github.openflocon.flocondesktop.features.analytics.domain.model.AnalyticsTableId
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageName
import kotlinx.coroutines.flow.Flow

interface DeviceAnalyticsDataSource {
    fun observeSelectedDeviceAnalytics(deviceIdAndPackageName: DeviceIdAndPackageName): Flow<AnalyticsIdentifierDomainModel?>
    fun selectDeviceAnalytics(
        deviceAnalytics: List<AnalyticsIdentifierDomainModel>,
        deviceIdAndPackageName: DeviceIdAndPackageName,
        analyticsTableId: AnalyticsTableId,
    )
}

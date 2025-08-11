package io.github.openflocon.data.local.analytics.datasource

import io.github.openflocon.data.core.analytics.datasource.DeviceAnalyticsDataSource
import io.github.openflocon.domain.analytics.models.AnalyticsIdentifierDomainModel
import io.github.openflocon.domain.analytics.models.AnalyticsTableId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class DeviceAnalyticsDataSourceInMemory : DeviceAnalyticsDataSource {
    private val selectedDeviceAnalytics = MutableStateFlow<Map<DeviceIdAndPackageNameDomainModel, AnalyticsIdentifierDomainModel?>>(emptyMap())

    override fun observeSelectedDeviceAnalytics(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<AnalyticsIdentifierDomainModel?> =
        selectedDeviceAnalytics
            .map { it[deviceIdAndPackageName] }
            .distinctUntilChanged()

    override fun selectDeviceAnalytics(
        deviceAnalytics: List<AnalyticsIdentifierDomainModel>,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        analyticsTableId: AnalyticsTableId,
    ) {
        val analytics = deviceAnalytics.firstOrNull { it.id == analyticsTableId } ?: return

        selectedDeviceAnalytics.update {
            it + (deviceIdAndPackageName to analytics)
        }
    }
}

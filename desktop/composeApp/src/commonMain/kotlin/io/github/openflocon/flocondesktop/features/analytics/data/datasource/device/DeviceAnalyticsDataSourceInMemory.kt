package io.github.openflocon.flocondesktop.features.analytics.data.datasource.device

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.features.analytics.domain.model.AnalyticsIdentifierDomainModel
import io.github.openflocon.flocondesktop.features.analytics.domain.model.AnalyticsTableId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class DeviceAnalyticsDataSourceInMemory : DeviceAnalyticsDataSource {
    private val selectedDeviceAnalytics =
        MutableStateFlow<Map<DeviceId, AnalyticsIdentifierDomainModel?>>(emptyMap())

    override fun observeSelectedDeviceAnalytics(deviceId: DeviceId): Flow<AnalyticsIdentifierDomainModel?> = selectedDeviceAnalytics
        .map {
            it[deviceId]
        }.distinctUntilChanged()

    override fun selectDeviceAnalytics(
        deviceAnalytics: List<AnalyticsIdentifierDomainModel>,
        deviceId: DeviceId,
        analyticsTableId: AnalyticsTableId,
    ) {
        val analytics = deviceAnalytics.firstOrNull { it.id == analyticsTableId } ?: return

        selectedDeviceAnalytics.update {
            it + (deviceId to analytics)
        }
    }
}

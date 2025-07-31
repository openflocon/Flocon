package io.github.openflocon.flocondesktop.features.dashboard.data.datasources.device

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.features.dashboard.domain.model.DashboardId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class DeviceDashboardsDataSourceInMemory : DeviceDashboardsDataSource {
    private val selectedDeviceDashboards =
        MutableStateFlow<Map<DeviceId, DashboardId?>>(emptyMap())

    override fun observeSelectedDeviceDashboard(deviceId: DeviceId): Flow<DashboardId?> = selectedDeviceDashboards
        .map {
            it[deviceId]
        }.distinctUntilChanged()

    override fun selectDeviceDashboard(
        deviceId: DeviceId,
        dashboardId: DashboardId,
    ) {
        selectedDeviceDashboards.update {
            it + (deviceId to dashboardId)
        }
    }
}

package io.github.openflocon.flocondesktop.features.dashboard.data.datasources.device

import io.github.openflocon.flocondesktop.features.dashboard.domain.model.DashboardId
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class DeviceDashboardsDataSourceInMemory : DeviceDashboardsDataSource {
    private val selectedDeviceDashboards = MutableStateFlow<Map<DeviceIdAndPackageName, DashboardId?>>(emptyMap())

    override fun observeSelectedDeviceDashboard(deviceIdAndPackageName: DeviceIdAndPackageName): Flow<DashboardId?> = selectedDeviceDashboards
        .map { it[deviceIdAndPackageName] }
        .distinctUntilChanged()

    override fun selectDeviceDashboard(deviceIdAndPackageName: DeviceIdAndPackageName, dashboardId: DashboardId) {
        selectedDeviceDashboards.update {
            it + (deviceIdAndPackageName to dashboardId)
        }
    }
}

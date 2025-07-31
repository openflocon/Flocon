package io.github.openflocon.flocondesktop.features.dashboard.data.datasources.device

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.features.dashboard.domain.model.DashboardId
import kotlinx.coroutines.flow.Flow

interface DeviceDashboardsDataSource {
    fun observeSelectedDeviceDashboard(deviceId: DeviceId): Flow<DashboardId?>
    fun selectDeviceDashboard(
        deviceId: DeviceId,
        dashboardId: DashboardId,
    )
}

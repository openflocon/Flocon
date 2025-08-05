package io.github.openflocon.flocondesktop.features.dashboard.data.datasources.device

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.features.dashboard.domain.model.DashboardId
import kotlinx.coroutines.flow.Flow

interface DeviceDashboardsDataSource {
    fun observeSelectedDeviceDashboard(deviceId: DeviceId): Flow<DashboardId?>
    fun selectDeviceDashboard(
        deviceId: DeviceId,
        dashboardId: DashboardId,
    )
}

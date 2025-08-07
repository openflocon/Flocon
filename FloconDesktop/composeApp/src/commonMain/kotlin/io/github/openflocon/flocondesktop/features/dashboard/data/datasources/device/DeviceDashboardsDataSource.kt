package io.github.openflocon.flocondesktop.features.dashboard.data.datasources.device

import io.github.openflocon.flocondesktop.features.dashboard.domain.model.DashboardId
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageName
import kotlinx.coroutines.flow.Flow

interface DeviceDashboardsDataSource {
    fun observeSelectedDeviceDashboard(deviceIdAndPackageName: DeviceIdAndPackageName): Flow<DashboardId?>
    fun selectDeviceDashboard(
        deviceIdAndPackageName: DeviceIdAndPackageName,
        dashboardId: DashboardId,
    )
}

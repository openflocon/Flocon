package io.github.openflocon.flocondesktop.features.dashboard.data.datasources.device

import io.github.openflocon.domain.dashboard.models.DashboardId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface DeviceDashboardsDataSource {
    fun observeSelectedDeviceDashboard(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<DashboardId?>
    fun selectDeviceDashboard(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        dashboardId: DashboardId,
    )
}

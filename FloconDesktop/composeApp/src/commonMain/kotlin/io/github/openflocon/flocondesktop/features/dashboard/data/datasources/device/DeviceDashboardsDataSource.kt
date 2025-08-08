package io.github.openflocon.flocondesktop.features.dashboard.data.datasources.device

import io.github.openflocon.flocondesktop.features.dashboard.domain.model.DashboardId
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface DeviceDashboardsDataSource {
    fun observeSelectedDeviceDashboard(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<DashboardId?>
    fun selectDeviceDashboard(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        dashboardId: DashboardId,
    )
}

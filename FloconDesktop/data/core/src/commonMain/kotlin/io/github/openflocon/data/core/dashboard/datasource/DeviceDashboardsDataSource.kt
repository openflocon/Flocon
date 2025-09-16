package io.github.openflocon.data.core.dashboard.datasource

import io.github.openflocon.domain.dashboard.models.DashboardId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface DeviceDashboardsDataSource {

    fun observeSelectedDeviceDashboard(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<DashboardId?>

    fun selectDeviceDashboard(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        dashboardId: DashboardId,
    )

    fun deleteDashboard(
        dashboardId: DashboardId,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel
    )
}

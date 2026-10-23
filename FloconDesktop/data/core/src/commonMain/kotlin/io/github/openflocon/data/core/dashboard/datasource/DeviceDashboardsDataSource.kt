package io.github.openflocon.data.core.dashboard.datasource

import io.github.openflocon.domain.dashboard.models.DashboardArrangementDomainModel
import io.github.openflocon.domain.dashboard.models.DashboardId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface DeviceDashboardsDataSource {

    fun observeSelectedDeviceDashboard(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel
    ): Flow<DashboardId?>

    fun selectDeviceDashboard(
        dashboardId: DashboardId,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    )

    fun deleteDashboard(
        dashboardId: DashboardId,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel
    )

    fun observeDashboardArrangement(
        dashboardId: DashboardId,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel
    ): Flow<DashboardArrangementDomainModel>

    fun selectDashboardArrangement(
        dashboardId: DashboardId,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        arrangement: DashboardArrangementDomainModel,
    )
}

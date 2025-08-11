package io.github.openflocon.data.core.dashboard.datasource

import io.github.openflocon.domain.dashboard.models.DashboardDomainModel
import io.github.openflocon.domain.dashboard.models.DashboardId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface DashboardLocalDataSource {
    suspend fun saveDashboard(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, dashboard: DashboardDomainModel)
    fun observeDashboard(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, dashboardId: DashboardId): Flow<DashboardDomainModel?>
    fun observeDeviceDashboards(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<DashboardId>>
}

package io.github.openflocon.flocondesktop.features.dashboard.data.datasources

import io.github.openflocon.domain.models.DashboardDomainModel
import io.github.openflocon.domain.models.DashboardId
import io.github.openflocon.domain.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface DashboardLocalDataSource {
    suspend fun saveDashboard(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, dashboard: DashboardDomainModel)
    fun observeDashboard(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, dashboardId: DashboardId): Flow<DashboardDomainModel?>
    fun observeDeviceDashboards(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<DashboardId>>
}

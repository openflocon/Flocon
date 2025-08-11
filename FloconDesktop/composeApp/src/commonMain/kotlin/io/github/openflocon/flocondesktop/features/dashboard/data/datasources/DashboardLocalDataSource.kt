package io.github.openflocon.flocondesktop.features.dashboard.data.datasources

import com.flocon.library.domain.models.DashboardDomainModel
import com.flocon.library.domain.models.DashboardId
import com.flocon.library.domain.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface DashboardLocalDataSource {
    suspend fun saveDashboard(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, dashboard: DashboardDomainModel)
    fun observeDashboard(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, dashboardId: DashboardId): Flow<DashboardDomainModel?>
    fun observeDeviceDashboards(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<DashboardId>>
}

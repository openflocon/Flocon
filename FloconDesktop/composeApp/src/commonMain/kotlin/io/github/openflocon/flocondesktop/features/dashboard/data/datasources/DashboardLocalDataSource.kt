package io.github.openflocon.flocondesktop.features.dashboard.data.datasources

import io.github.openflocon.flocondesktop.features.dashboard.domain.model.DashboardDomainModel
import io.github.openflocon.flocondesktop.features.dashboard.domain.model.DashboardId
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageName
import kotlinx.coroutines.flow.Flow

interface DashboardLocalDataSource {
    suspend fun saveDashboard(deviceIdAndPackageName: DeviceIdAndPackageName, dashboard: DashboardDomainModel)
    fun observeDashboard(deviceIdAndPackageName: DeviceIdAndPackageName, dashboardId: DashboardId): Flow<DashboardDomainModel?>
    fun observeDeviceDashboards(deviceIdAndPackageName: DeviceIdAndPackageName): Flow<List<DashboardId>>
}

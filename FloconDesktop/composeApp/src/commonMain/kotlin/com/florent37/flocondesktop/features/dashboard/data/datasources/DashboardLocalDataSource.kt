package com.florent37.flocondesktop.features.dashboard.data.datasources

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.features.dashboard.domain.model.DashboardDomainModel
import com.florent37.flocondesktop.features.dashboard.domain.model.DashboardId
import kotlinx.coroutines.flow.Flow

interface DashboardLocalDataSource {
    suspend fun saveDashboard(deviceId: String, dashboard: DashboardDomainModel)
    fun observeDashboard(deviceId: String, dashboardId: DashboardId): Flow<DashboardDomainModel?>
    fun observeDeviceDashboards(deviceId: DeviceId): Flow<List<DashboardId>>
}

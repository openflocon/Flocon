package io.github.openflocon.data.local.dashboard.datasource

import io.github.openflocon.data.core.dashboard.datasource.DeviceDashboardsDataSource
import io.github.openflocon.domain.dashboard.models.DashboardId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class DeviceDashboardsDataSourceInMemory : DeviceDashboardsDataSource {
    private val selectedDeviceDashboards = MutableStateFlow<Map<DeviceIdAndPackageNameDomainModel, DashboardId?>>(emptyMap())

    override fun observeSelectedDeviceDashboard(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<DashboardId?> = selectedDeviceDashboards
        .map { it[deviceIdAndPackageName] }
        .distinctUntilChanged()

    override fun selectDeviceDashboard(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, dashboardId: DashboardId) {
        selectedDeviceDashboards.update {
            it + (deviceIdAndPackageName to dashboardId)
        }
    }
}

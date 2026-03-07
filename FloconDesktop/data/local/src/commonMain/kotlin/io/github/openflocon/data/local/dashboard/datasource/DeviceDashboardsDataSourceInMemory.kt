package io.github.openflocon.data.local.dashboard.datasource

import io.github.openflocon.data.core.dashboard.datasource.DeviceDashboardsDataSource
import io.github.openflocon.domain.dashboard.models.DashboardArrangementDomainModel
import io.github.openflocon.domain.dashboard.models.DashboardId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class DeviceDashboardsDataSourceInMemory : DeviceDashboardsDataSource {
    private val selectedDeviceDashboards = MutableStateFlow<Map<DeviceIdAndPackageNameDomainModel, DashboardId?>>(emptyMap())
    private val selectedDashboardArrangements = MutableStateFlow<Map<String, DashboardArrangementDomainModel>>(emptyMap())

    override fun observeSelectedDeviceDashboard(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<DashboardId?> = selectedDeviceDashboards
        .map { it[deviceIdAndPackageName] }
        .distinctUntilChanged()

    override fun selectDeviceDashboard(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, dashboardId: DashboardId) {
        selectedDeviceDashboards.update {
            it + (deviceIdAndPackageName to dashboardId)
        }
    }

    override fun deleteDashboard(dashboardId: DashboardId, deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) {
        selectedDeviceDashboards.update {
            if (it[deviceIdAndPackageName] == dashboardId) {
                it - deviceIdAndPackageName
            } else it
        }
    }

    override fun observeDashboardArrangement(dashboardId: DashboardId, deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<DashboardArrangementDomainModel> = selectedDashboardArrangements
        .map {
            val dashboardKey = getDashboardArrangementKey(dashboardId, deviceIdAndPackageName)
            it[dashboardKey] ?: DashboardArrangementDomainModel.Adaptive
        }
        .distinctUntilChanged()

    override fun selectDashboardArrangement(
        dashboardId: DashboardId,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        arrangement: DashboardArrangementDomainModel
    ) {
        val dashboardKey = getDashboardArrangementKey(dashboardId, deviceIdAndPackageName)
        selectedDashboardArrangements.update {
            it + (dashboardKey to arrangement)
        }
    }

    private fun getDashboardArrangementKey(
        dashboardId: DashboardId,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel
    ): String {
        return "${deviceIdAndPackageName.packageName}_$dashboardId"
    }
}

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
    private val selectedDeviceDashboards = MutableStateFlow<Map<String, DashboardId?>>(emptyMap())
    private val selectedDashboardArrangements = MutableStateFlow<Map<String, DashboardArrangementDomainModel>>(emptyMap())

    override fun observeSelectedDeviceDashboard(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel
    ): Flow<DashboardId?> = selectedDeviceDashboards
        .map {
            val dashboardKey = getSelectedDashboardKey(deviceIdAndPackageName)
            it[dashboardKey]
        }
        .distinctUntilChanged()

    override fun selectDeviceDashboard(
        dashboardId: DashboardId,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel
    ) {
        val dashboardKey = getSelectedDashboardKey(deviceIdAndPackageName)
        selectedDeviceDashboards.update {
            it + (dashboardKey to dashboardId)
        }
    }

    override fun deleteDashboard(
        dashboardId: DashboardId,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel
    ) {
        val dashboardKey = getSelectedDashboardKey(deviceIdAndPackageName)
        selectedDeviceDashboards.update {
            if (it[dashboardKey] == dashboardId) {
                it - dashboardKey
            } else it
        }
    }

    override fun observeDashboardArrangement(
        dashboardId: DashboardId,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel
    ): Flow<DashboardArrangementDomainModel> = selectedDashboardArrangements
        .map {
            val dashboardArrangementKey = getDashboardArrangementKey(dashboardId, deviceIdAndPackageName)
            it[dashboardArrangementKey] ?: DashboardArrangementDomainModel.Adaptive
        }
        .distinctUntilChanged()

    override fun selectDashboardArrangement(
        dashboardId: DashboardId,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        arrangement: DashboardArrangementDomainModel
    ) {
        val dashboardArrangementKey = getDashboardArrangementKey(dashboardId, deviceIdAndPackageName)
        selectedDashboardArrangements.update {
            it + (dashboardArrangementKey to arrangement)
        }
    }

    private fun getSelectedDashboardKey(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel
    ): String {
        return deviceIdAndPackageName.packageName
    }

    private fun getDashboardArrangementKey(
        dashboardId: DashboardId,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel
    ): String {
        return "${deviceIdAndPackageName.packageName}_$dashboardId"
    }
}

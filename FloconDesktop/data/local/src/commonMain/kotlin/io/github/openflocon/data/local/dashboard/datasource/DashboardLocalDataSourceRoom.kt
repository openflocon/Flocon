package io.github.openflocon.data.local.dashboard.datasource

import io.github.openflocon.data.core.dashboard.datasource.DashboardLocalDataSource
import io.github.openflocon.data.local.dashboard.dao.FloconDashboardDao
import io.github.openflocon.data.local.dashboard.mapper.toDomain
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.dashboard.models.DashboardDomainModel
import io.github.openflocon.domain.dashboard.models.DashboardId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class DashboardLocalDataSourceRoom(
    private val dashboardDao: FloconDashboardDao,
    private val dispatcherProvider: DispatcherProvider,
) : DashboardLocalDataSource {

    override suspend fun saveDashboard(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, dashboard: DashboardDomainModel) {
        withContext(dispatcherProvider.data) {
            dashboardDao.saveFullDashboard(
                deviceId = deviceIdAndPackageName.deviceId,
                packageName = deviceIdAndPackageName.packageName,
                dashboard = dashboard,
            )
        }
    }

    override fun observeDashboard(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, dashboardId: DashboardId): Flow<DashboardDomainModel?> =
        dashboardDao.observeDashboardWithSectionsAndElements(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
            dashboardId = dashboardId,
        )
            .map { it?.toDomain() }
            .distinctUntilChanged()
            .flowOn(dispatcherProvider.data)

    override fun observeDeviceDashboards(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<DashboardId>> =
        dashboardDao.observeDeviceDashboards(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
        )
            .flowOn(dispatcherProvider.data)
}

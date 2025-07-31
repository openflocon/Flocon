package io.github.openflocon.flocondesktop.features.dashboard.data.datasources.room

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import io.github.openflocon.flocondesktop.features.dashboard.data.datasources.DashboardLocalDataSource
import io.github.openflocon.flocondesktop.features.dashboard.data.datasources.room.mapper.toDomain
import io.github.openflocon.flocondesktop.features.dashboard.domain.model.DashboardDomainModel
import io.github.openflocon.flocondesktop.features.dashboard.domain.model.DashboardId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class DashboardLocalDataSourceRoom(
    private val dashboardDao: FloconDashboardDao,
    private val dispatcherProvider: DispatcherProvider,
) : DashboardLocalDataSource {

    override suspend fun saveDashboard(deviceId: String, dashboard: DashboardDomainModel) {
        withContext(dispatcherProvider.data) {
            dashboardDao.saveFullDashboard(
                deviceId = deviceId,
                dashboard = dashboard,
            )
        }
    }

    override fun observeDashboard(
        deviceId: String,
        dashboardId: DashboardId,
    ): Flow<DashboardDomainModel?> = dashboardDao.observeDashboardWithSectionsAndElements(
        deviceId = deviceId,
        dashboardId = dashboardId,
    )
        .map {
            it?.toDomain()
        }
        .distinctUntilChanged()
        .flowOn(dispatcherProvider.data)

    override fun observeDeviceDashboards(deviceId: DeviceId): Flow<List<DashboardId>> = dashboardDao.observeDeviceDashboards(
        deviceId = deviceId,
    ).flowOn(dispatcherProvider.data)
}

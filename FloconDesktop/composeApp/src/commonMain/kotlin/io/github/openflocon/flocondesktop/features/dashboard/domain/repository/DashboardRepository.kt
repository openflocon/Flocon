package io.github.openflocon.flocondesktop.features.dashboard.domain.repository

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.features.dashboard.domain.model.DashboardDomainModel
import io.github.openflocon.flocondesktop.features.dashboard.domain.model.DashboardId
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {
    fun observeDashboard(deviceId: DeviceId, dashboardId: DashboardId): Flow<DashboardDomainModel?>

    suspend fun selectDeviceDashboard(deviceId: DeviceId, dashboardId: DashboardId)
    fun observeSelectedDeviceDashboard(deviceId: DeviceId): Flow<DashboardId?>
    fun observeDeviceDashboards(deviceId: DeviceId): Flow<List<DashboardId>>

    suspend fun sendClickEvent(
        deviceId: DeviceId,
        dashboardId: DashboardId,
        buttonId: String,
    )

    suspend fun submitTextFieldEvent(
        deviceId: DeviceId,
        dashboardId: DashboardId,
        textFieldId: String,
        value: String,
    )

    suspend fun sendUpdateCheckBoxEvent(
        deviceId: DeviceId,
        dashboardId: DashboardId,
        checkBoxId: String,
        value: Boolean,
    )
}

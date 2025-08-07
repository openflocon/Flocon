package io.github.openflocon.flocondesktop.features.dashboard.domain.repository

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.features.dashboard.domain.model.DashboardDomainModel
import io.github.openflocon.flocondesktop.features.dashboard.domain.model.DashboardId
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageName
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {
    fun observeDashboard(deviceIdAndPackageName: DeviceIdAndPackageName, dashboardId: DashboardId): Flow<DashboardDomainModel?>

    suspend fun selectDeviceDashboard(deviceIdAndPackageName: DeviceIdAndPackageName, dashboardId: DashboardId)
    fun observeSelectedDeviceDashboard(deviceIdAndPackageName: DeviceIdAndPackageName): Flow<DashboardId?>
    fun observeDeviceDashboards(deviceIdAndPackageName: DeviceIdAndPackageName): Flow<List<DashboardId>>

    suspend fun sendClickEvent(
        deviceIdAndPackageName: DeviceIdAndPackageName,
        dashboardId: DashboardId,
        buttonId: String,
    )

    suspend fun submitTextFieldEvent(
        deviceIdAndPackageName: DeviceIdAndPackageName,
        dashboardId: DashboardId,
        textFieldId: String,
        value: String,
    )

    suspend fun sendUpdateCheckBoxEvent(
        deviceIdAndPackageName: DeviceIdAndPackageName,
        dashboardId: DashboardId,
        checkBoxId: String,
        value: Boolean,
    )
}

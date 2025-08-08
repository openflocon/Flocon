package io.github.openflocon.flocondesktop.features.dashboard.domain.repository

import io.github.openflocon.flocondesktop.features.dashboard.domain.model.DashboardDomainModel
import io.github.openflocon.flocondesktop.features.dashboard.domain.model.DashboardId
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {
    fun observeDashboard(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, dashboardId: DashboardId): Flow<DashboardDomainModel?>

    suspend fun selectDeviceDashboard(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, dashboardId: DashboardId)
    fun observeSelectedDeviceDashboard(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<DashboardId?>
    fun observeDeviceDashboards(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<DashboardId>>

    suspend fun sendClickEvent(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        dashboardId: DashboardId,
        buttonId: String,
    )

    suspend fun submitTextFieldEvent(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        dashboardId: DashboardId,
        textFieldId: String,
        value: String,
    )

    suspend fun sendUpdateCheckBoxEvent(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        dashboardId: DashboardId,
        checkBoxId: String,
        value: Boolean,
    )
}

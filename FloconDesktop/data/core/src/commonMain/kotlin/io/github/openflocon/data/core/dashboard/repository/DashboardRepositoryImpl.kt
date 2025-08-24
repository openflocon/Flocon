package io.github.openflocon.data.core.dashboard.repository

import io.github.openflocon.data.core.dashboard.datasource.DashboardLocalDataSource
import io.github.openflocon.data.core.dashboard.datasource.DeviceDashboardsDataSource
import io.github.openflocon.data.core.dashboard.datasource.ToDeviceDashboardDataSource
import io.github.openflocon.domain.Protocol
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.dashboard.models.DashboardDomainModel
import io.github.openflocon.domain.dashboard.models.DashboardId
import io.github.openflocon.domain.dashboard.repository.DashboardRepository
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.messages.repository.MessagesReceiverRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class DashboardRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val dashboardLocalDataSource: DashboardLocalDataSource,
    private val toDeviceDashboardDataSource: ToDeviceDashboardDataSource,
    private val deviceDashboardsDataSource: DeviceDashboardsDataSource,
) : DashboardRepository,
    MessagesReceiverRepository {

    override val pluginName = listOf(Protocol.FromDevice.Dashboard.Plugin)

    override suspend fun onMessageReceived(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        message: FloconIncomingMessageDomainModel
    ) {
        withContext(dispatcherProvider.data) {
            val item = toDeviceDashboardDataSource.getItem(message) ?: return@withContext

            dashboardLocalDataSource.saveDashboard(
                deviceIdAndPackageName = deviceIdAndPackageName,
                dashboard = item
            )
        }
    }

    override suspend fun onDeviceConnected(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        isNewDevice: Boolean,
    ) {
        // no op
    }


    override fun observeDashboard(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, dashboardId: DashboardId): Flow<DashboardDomainModel?> =
        dashboardLocalDataSource.observeDashboard(
            deviceIdAndPackageName = deviceIdAndPackageName,
            dashboardId = dashboardId,
        )
            .flowOn(dispatcherProvider.data)

    override suspend fun sendClickEvent(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, dashboardId: DashboardId, buttonId: String) {
        withContext(dispatcherProvider.data) {
            toDeviceDashboardDataSource.sendClickEvent(
                deviceIdAndPackageName = deviceIdAndPackageName,
                buttonId = buttonId,
            )
        }
    }

    override suspend fun submitTextFieldEvent(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        dashboardId: DashboardId,
        textFieldId: String,
        value: String,
    ) {
        withContext(dispatcherProvider.data) {
            toDeviceDashboardDataSource.submitTextFieldEvent(
                deviceIdAndPackageName = deviceIdAndPackageName,
                textFieldId = textFieldId,
                value = value,
            )
        }
    }

    override suspend fun sendUpdateCheckBoxEvent(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        dashboardId: DashboardId,
        checkBoxId: String,
        value: Boolean,
    ) {
        withContext(dispatcherProvider.data) {
            toDeviceDashboardDataSource.sendUpdateCheckBoxEvent(
                deviceIdAndPackageName = deviceIdAndPackageName,
                checkBoxId = checkBoxId,
                value = value,
            )
        }
    }

    override suspend fun selectDeviceDashboard(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, dashboardId: DashboardId) {
        withContext(dispatcherProvider.data) {
            deviceDashboardsDataSource.selectDeviceDashboard(
                deviceIdAndPackageName = deviceIdAndPackageName,
                dashboardId = dashboardId,
            )
        }
    }

    override fun observeSelectedDeviceDashboard(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<DashboardId?> = deviceDashboardsDataSource.observeSelectedDeviceDashboard(
        deviceIdAndPackageName = deviceIdAndPackageName,
    )

    override fun observeDeviceDashboards(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<DashboardId>> = dashboardLocalDataSource.observeDeviceDashboards(
        deviceIdAndPackageName = deviceIdAndPackageName,
    )
}

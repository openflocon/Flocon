package io.github.openflocon.flocondesktop.features.dashboard.data

import com.flocon.data.remote.Protocol
import com.flocon.data.remote.models.FloconIncomingMessageDataModel
import io.github.openflocon.data.core.dashboard.datasource.DashboardLocalDataSource
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.dashboard.models.DashboardDomainModel
import io.github.openflocon.domain.dashboard.models.DashboardId
import io.github.openflocon.domain.dashboard.repository.DashboardRepository
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.flocondesktop.features.dashboard.data.datasources.ToDeviceDashboardDataSource
import io.github.openflocon.flocondesktop.features.dashboard.data.datasources.device.DeviceDashboardsDataSource
import io.github.openflocon.flocondesktop.features.dashboard.data.mapper.toDomain
import io.github.openflocon.flocondesktop.features.dashboard.data.model.DashboardConfigDataModel
import io.github.openflocon.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class DashboardRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val dashboardLocalDataSource: DashboardLocalDataSource,
    private val toDeviceDashboardDataSource: ToDeviceDashboardDataSource,
    private val deviceDashboardsDataSource: DeviceDashboardsDataSource,
) : DashboardRepository,
    MessagesReceiverRepository {

    override val pluginName = listOf(Protocol.FromDevice.Dashboard.Plugin)

    // maybe inject
    private val dashboardParser =
        Json {
            ignoreUnknownKeys = true
        }

    override suspend fun onNewDevice(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) {
        // no op
    }

    override suspend fun onMessageReceived(
        deviceId: String,
        message: FloconIncomingMessageDataModel,
    ) {
        withContext(dispatcherProvider.data) {
            decode(message)?.let { toDomain(it) }?.let { request ->
                dashboardLocalDataSource.saveDashboard(
                    deviceIdAndPackageName = DeviceIdAndPackageNameDomainModel(
                        deviceId = deviceId,
                        packageName = message.appPackageName,
                    ),
                    dashboard = request,
                )
            }
        }
    }

    private fun decode(message: FloconIncomingMessageDataModel): DashboardConfigDataModel? = try {
        dashboardParser.decodeFromString<DashboardConfigDataModel>(message.body)
    } catch (t: Throwable) {
        t.printStackTrace()
        null
    }

    override fun observeDashboard(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, dashboardId: DashboardId): Flow<DashboardDomainModel?> = dashboardLocalDataSource.observeDashboard(
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

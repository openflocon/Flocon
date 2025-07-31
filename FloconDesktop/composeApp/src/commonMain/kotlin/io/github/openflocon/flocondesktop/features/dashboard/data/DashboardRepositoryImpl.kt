package io.github.openflocon.flocondesktop.features.dashboard.data

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.FloconIncomingMessageDataModel
import com.florent37.flocondesktop.Protocol
import com.florent37.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import com.florent37.flocondesktop.features.dashboard.data.datasources.DashboardLocalDataSource
import com.florent37.flocondesktop.features.dashboard.data.datasources.ToDeviceDashboardDataSource
import com.florent37.flocondesktop.features.dashboard.data.datasources.device.DeviceDashboardsDataSource
import com.florent37.flocondesktop.features.dashboard.data.mapper.toDomain
import com.florent37.flocondesktop.features.dashboard.data.model.DashboardConfigDataModel
import com.florent37.flocondesktop.features.dashboard.domain.model.DashboardDomainModel
import com.florent37.flocondesktop.features.dashboard.domain.model.DashboardId
import com.florent37.flocondesktop.features.dashboard.domain.repository.DashboardRepository
import com.florent37.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
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

    override suspend fun onMessageReceived(
        deviceId: String,
        message: FloconIncomingMessageDataModel,
    ) {
        withContext(dispatcherProvider.data) {
            decode(message)?.let { toDomain(it) }?.let { request ->
                dashboardLocalDataSource.saveDashboard(deviceId = deviceId, dashboard = request)
            }
        }
    }

    private fun decode(message: FloconIncomingMessageDataModel): DashboardConfigDataModel? = try {
        dashboardParser.decodeFromString<DashboardConfigDataModel>(message.body)
    } catch (t: Throwable) {
        t.printStackTrace()
        null
    }

    override fun observeDashboard(
        deviceId: DeviceId,
        dashboardId: DashboardId,
    ): Flow<DashboardDomainModel?> = dashboardLocalDataSource.observeDashboard(
        deviceId = deviceId,
        dashboardId = dashboardId,
    )
        .flowOn(dispatcherProvider.data)

    override suspend fun sendClickEvent(
        deviceId: DeviceId,
        dashboardId: DashboardId,
        buttonId: String,
    ) {
        withContext(dispatcherProvider.data) {
            toDeviceDashboardDataSource.sendClickEvent(
                deviceId = deviceId,
                buttonId = buttonId,
            )
        }
    }

    override suspend fun submitTextFieldEvent(
        deviceId: DeviceId,
        dashboardId: DashboardId,
        textFieldId: String,
        value: String,
    ) {
        withContext(dispatcherProvider.data) {
            toDeviceDashboardDataSource.submitTextFieldEvent(
                deviceId = deviceId,
                textFieldId = textFieldId,
                value = value,
            )
        }
    }

    override suspend fun sendUpdateCheckBoxEvent(
        deviceId: DeviceId,
        dashboardId: DashboardId,
        checkBoxId: String,
        value: Boolean,
    ) {
        withContext(dispatcherProvider.data) {
            toDeviceDashboardDataSource.sendUpdateCheckBoxEvent(
                deviceId = deviceId,
                checkBoxId = checkBoxId,
                value = value,
            )
        }
    }

    override suspend fun selectDeviceDashboard(
        deviceId: DeviceId,
        dashboardId: DashboardId,
    ) {
        withContext(dispatcherProvider.data) {
            deviceDashboardsDataSource.selectDeviceDashboard(
                deviceId = deviceId,
                dashboardId = dashboardId,
            )
        }
    }

    override fun observeSelectedDeviceDashboard(deviceId: DeviceId): Flow<DashboardId?> = deviceDashboardsDataSource.observeSelectedDeviceDashboard(
        deviceId = deviceId,
    )

    override fun observeDeviceDashboards(deviceId: DeviceId): Flow<List<DashboardId>> = dashboardLocalDataSource.observeDeviceDashboards(
        deviceId = deviceId,
    )
}

package com.flocon.data.remote.dashboard.datasource

import com.flocon.data.remote.dashboard.models.ToDeviceCheckBoxValueChangedMessage
import com.flocon.data.remote.dashboard.models.ToDeviceSubmittedTextFieldMessage
import com.flocon.data.remote.models.FloconOutgoingMessageDataModel
import com.flocon.data.remote.models.toRemote
import com.flocon.data.remote.server.Server
import io.github.openflocon.data.core.dashboard.datasource.ToDeviceDashboardDataSource
import io.github.openflocon.domain.Protocol
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.serialization.json.Json
import kotlin.uuid.ExperimentalUuidApi

class ToDeviceDashboardDataSourceImpl(
    private val server: Server,
) : ToDeviceDashboardDataSource {

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun sendClickEvent(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        buttonId: String,
    ) {
        server.sendMessageToClient(
            deviceIdAndPackageName = deviceIdAndPackageName.toRemote(),
            message = FloconOutgoingMessageDataModel(
                plugin = Protocol.ToDevice.Dashboard.Plugin,
                method = Protocol.ToDevice.Dashboard.Method.OnClick,
                body = buttonId,
            ),
        )
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun submitTextFieldEvent(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        textFieldId: String,
        value: String,
    ) {
        server.sendMessageToClient(
            deviceIdAndPackageName = deviceIdAndPackageName.toRemote(),
            message = FloconOutgoingMessageDataModel(
                plugin = Protocol.ToDevice.Dashboard.Plugin,
                method = Protocol.ToDevice.Dashboard.Method.OnTextFieldSubmitted,
                body = Json.Default.encodeToString(
                    ToDeviceSubmittedTextFieldMessage(
                        id = textFieldId,
                        value = value,
                    ),
                ),
            ),
        )
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun sendUpdateCheckBoxEvent(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        checkBoxId: String,
        value: Boolean,
    ) {
        server.sendMessageToClient(
            deviceIdAndPackageName = deviceIdAndPackageName.toRemote(),
            message = FloconOutgoingMessageDataModel(
                plugin = Protocol.ToDevice.Dashboard.Plugin,
                method = Protocol.ToDevice.Dashboard.Method.OnCheckBoxValueChanged,
                body = Json.Default.encodeToString(
                    ToDeviceCheckBoxValueChangedMessage(
                        id = checkBoxId,
                        value = value,
                    ),
                ),
            ),
        )
    }
}

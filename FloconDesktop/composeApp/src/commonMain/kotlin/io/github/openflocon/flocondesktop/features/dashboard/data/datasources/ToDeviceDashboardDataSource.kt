package io.github.openflocon.flocondesktop.features.dashboard.data.datasources

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.FloconOutgoingMessageDataModel
import io.github.openflocon.flocondesktop.Protocol
import io.github.openflocon.flocondesktop.Server
import io.github.openflocon.flocondesktop.features.dashboard.data.datasources.remote.model.ToDeviceCheckBoxValueChangedMessage
import io.github.openflocon.flocondesktop.features.dashboard.data.datasources.remote.model.ToDeviceSubmittedTextFieldMessage
import kotlinx.serialization.json.Json
import kotlin.uuid.ExperimentalUuidApi

class ToDeviceDashboardDataSource(
    private val server: Server,
) {

    @OptIn(ExperimentalUuidApi::class)
    suspend fun sendClickEvent(
        deviceId: DeviceId,
        buttonId: String,
    ) {
        server.sendMessageToClient(
            deviceId = deviceId,
            message = FloconOutgoingMessageDataModel(
                plugin = Protocol.ToDevice.Dashboard.Plugin,
                method = Protocol.ToDevice.Dashboard.Method.OnClick,
                body = buttonId,
            ),
        )
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun submitTextFieldEvent(
        deviceId: DeviceId,
        textFieldId: String,
        value: String,
    ) {
        server.sendMessageToClient(
            deviceId = deviceId,
            message = FloconOutgoingMessageDataModel(
                plugin = Protocol.ToDevice.Dashboard.Plugin,
                method = Protocol.ToDevice.Dashboard.Method.OnTextFieldSubmitted,
                body = Json.encodeToString(
                    ToDeviceSubmittedTextFieldMessage(
                        id = textFieldId,
                        value = value,
                    ),
                ),
            ),
        )
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun sendUpdateCheckBoxEvent(
        deviceId: DeviceId,
        checkBoxId: String,
        value: Boolean,
    ) {
        server.sendMessageToClient(
            deviceId = deviceId,
            message = FloconOutgoingMessageDataModel(
                plugin = Protocol.ToDevice.Dashboard.Plugin,
                method = Protocol.ToDevice.Dashboard.Method.OnCheckBoxValueChanged,
                body = Json.encodeToString(
                    ToDeviceCheckBoxValueChangedMessage(
                        id = checkBoxId,
                        value = value,
                    ),
                ),
            ),
        )
    }
}

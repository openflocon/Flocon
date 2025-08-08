package io.github.openflocon.flocondesktop.features.dashboard.data.datasources

import io.github.openflocon.flocondesktop.FloconOutgoingMessageDataModel
import io.github.openflocon.flocondesktop.Protocol
import io.github.openflocon.flocondesktop.Server
import io.github.openflocon.flocondesktop.features.dashboard.data.datasources.remote.model.ToDeviceCheckBoxValueChangedMessage
import io.github.openflocon.flocondesktop.features.dashboard.data.datasources.remote.model.ToDeviceSubmittedTextFieldMessage
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.flocondesktop.messages.domain.model.toRemote
import kotlinx.serialization.json.Json
import kotlin.uuid.ExperimentalUuidApi

class ToDeviceDashboardDataSource(
    private val server: Server,
) {

    @OptIn(ExperimentalUuidApi::class)
    suspend fun sendClickEvent(
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
    suspend fun submitTextFieldEvent(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        textFieldId: String,
        value: String,
    ) {
        server.sendMessageToClient(
            deviceIdAndPackageName = deviceIdAndPackageName.toRemote(),
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
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        checkBoxId: String,
        value: Boolean,
    ) {
        server.sendMessageToClient(
            deviceIdAndPackageName = deviceIdAndPackageName.toRemote(),
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

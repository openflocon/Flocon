package com.flocon.data.remote.accessibility.datasource

import com.flocon.data.remote.models.FloconOutgoingMessageDataModel
import com.flocon.data.remote.models.toRemote
import com.flocon.data.remote.server.Server
import io.github.openflocon.data.core.accessibility.datasource.ToDeviceAccessibilityDataSource
import io.github.openflocon.domain.Protocol
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel

class ToDeviceAccessibilityDataSourceImpl(
    private val server: Server,
) : ToDeviceAccessibilityDataSource {

    override suspend fun requestFocusedNode(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) {
        send(deviceIdAndPackageName, Protocol.ToDevice.Accessibility.Method.GetFocusedNode)
    }

    override suspend fun focusNext(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) {
        send(deviceIdAndPackageName, Protocol.ToDevice.Accessibility.Method.FocusNext)
    }

    override suspend fun focusPrevious(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) {
        send(deviceIdAndPackageName, Protocol.ToDevice.Accessibility.Method.FocusPrevious)
    }

    override suspend fun setTalkBackEnabled(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, enabled: Boolean) {
        send(
            deviceIdAndPackageName,
            if (enabled) {
                Protocol.ToDevice.Accessibility.Method.EnableTalkBack
            } else {
                Protocol.ToDevice.Accessibility.Method.DisableTalkBack
            },
        )
    }

    private suspend fun send(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, method: String) {
        server.sendMessageToClient(
            deviceIdAndPackageName = deviceIdAndPackageName.toRemote(),
            message = FloconOutgoingMessageDataModel(
                plugin = Protocol.ToDevice.Accessibility.Plugin,
                method = method,
                body = "",
            ),
        )
    }
}

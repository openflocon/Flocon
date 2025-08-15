package io.github.openflocon.flocon.plugins.device

import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.model.FloconMessageFromServer
import io.github.openflocon.flocon.plugins.device.model.fromdevice.RegisterDeviceDataModel

class FloconDevicePluginImpl(
    private var sender: FloconMessageSender,
) : FloconDevicePlugin {

    override fun registerWithSerial(serial: String) {
        sender.send(
            plugin = Protocol.FromDevice.Device.Plugin,
            method = Protocol.FromDevice.Device.Method.RegisterDevice,
            body = RegisterDeviceDataModel(serial).toJson().toString(),
        )
    }

    override fun onMessageReceived(
        messageFromServer: FloconMessageFromServer,
        sender: FloconMessageSender
    ) {
        // no op
    }

    override fun onConnectedToServer(sender: FloconMessageSender) {
        // no op
    }
}
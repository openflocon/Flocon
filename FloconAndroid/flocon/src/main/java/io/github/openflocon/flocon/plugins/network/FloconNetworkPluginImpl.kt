package io.github.openflocon.flocon.plugins.network

import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.model.FloconMessageFromServer
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkRequest
import io.github.openflocon.flocon.plugins.network.model.floconNetworkRequestToJson

class FloconNetworkPluginImpl(
    private var sender: FloconMessageSender,
) : FloconNetworkPlugin {

    override fun log(call: FloconNetworkRequest) {
        sender.send(
            plugin = Protocol.FromDevice.Network.Plugin,
            method = Protocol.FromDevice.Network.Method.LogNetworkCall,
            body = floconNetworkRequestToJson(call).toString(),
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
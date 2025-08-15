package io.github.openflocon.flocon.plugins.device

import io.github.openflocon.flocon.core.FloconPlugin
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkCall
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkCallRequest
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkCallResponse
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkRequest
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkResponse
import io.github.openflocon.flocon.plugins.network.model.MockNetworkResponse

interface FloconDevicePlugin : FloconPlugin {
    fun registerWithSerial(serial: String)
}
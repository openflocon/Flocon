package io.github.openflocon.flocon.plugins.network

import io.github.openflocon.flocon.core.FloconPlugin
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkRequest

interface FloconNetworkPlugin : FloconPlugin {
    fun log(call: FloconNetworkRequest)
}
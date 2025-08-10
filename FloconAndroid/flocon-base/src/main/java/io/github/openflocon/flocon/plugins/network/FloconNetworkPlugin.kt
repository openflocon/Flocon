package io.github.openflocon.flocon.plugins.network

import io.github.openflocon.flocon.core.FloconPlugin
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkRequest
import io.github.openflocon.flocon.plugins.network.model.MockNetworkResponse

interface FloconNetworkPlugin : FloconPlugin {
    val mocks: Collection<MockNetworkResponse>
    fun log(call: FloconNetworkRequest)
}
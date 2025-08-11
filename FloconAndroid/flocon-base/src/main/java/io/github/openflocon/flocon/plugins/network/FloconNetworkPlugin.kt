package io.github.openflocon.flocon.plugins.network

import io.github.openflocon.flocon.core.FloconPlugin
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkCall
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkRequest
import io.github.openflocon.flocon.plugins.network.model.MockNetworkResponse

interface FloconNetworkPlugin : FloconPlugin {
    val mocks: Collection<MockNetworkResponse>

    @Deprecated("use logRequest & logResponse")
    fun log(call: FloconNetworkCall)

    fun logRequest(request: FloconNetworkRequest)
    fun logResponse(response: FloconNetworkRequest)
}
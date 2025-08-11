package io.github.openflocon.flocon.grpc

import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkCall
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkRequest
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkResponse
import java.util.concurrent.ConcurrentHashMap

internal class FloconGrpcPlugin() {

    private val requests = ConcurrentHashMap<String,FloconNetworkRequest>()

    fun reportRequest(callId: String, request: FloconNetworkRequest) {
        requests[callId] = request
    }

    fun reportResponse(callId: String, response: FloconNetworkResponse) {
        val request = requests[callId] ?: return
        val responseTime = System.currentTimeMillis()
        val durationMs = (responseTime - request.startTime).toDouble()
        val call = FloconNetworkCall(
            durationMs = durationMs,
            request = request,
            response = response,
            floconNetworkType = "grpc",
            isMocked = false,
        )

        FloconApp.instance?.client?.networkPlugin?.log(call)

        requests.remove(callId)
    }
}

package io.github.openflocon.flocon.grpc

import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkCallRequest
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkCallResponse
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkRequest
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkResponse
import java.util.concurrent.ConcurrentHashMap

internal class FloconGrpcPlugin() {

    private val requests = ConcurrentHashMap<String, FloconNetworkRequest>()

    fun reportRequest(callId: String, request: FloconNetworkRequest) {
        requests[callId] = request
        FloconApp.instance?.client?.networkPlugin?.logRequest(
            FloconNetworkCallRequest(
                floconCallId = callId,
                floconNetworkType = "grpc",
                request = request,
                isMocked = false,
            )
        )
    }

    fun reportResponse(callId: String, response: FloconNetworkResponse) {
        val request = requests[callId] ?: return

        val responseTime = System.currentTimeMillis()
        val durationMs = (responseTime - request.startTime).toDouble()

        FloconApp.instance?.client?.networkPlugin?.logResponse(FloconNetworkCallResponse(
            floconCallId = callId,
            floconNetworkType = "grpc",
            response = response,
            durationMs = durationMs,
            isMocked = false,
        ))

        requests.remove(callId)
    }
}

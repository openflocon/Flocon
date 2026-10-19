package io.github.openflocon.flocon.grpc

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.network.core.model.FloconNetworkCallRequest
import io.github.openflocon.flocon.network.core.model.FloconNetworkCallResponse
import io.github.openflocon.flocon.network.core.model.FloconNetworkRequest
import io.github.openflocon.flocon.network.core.model.FloconNetworkResponse
import io.github.openflocon.flocon.network.core.networkPluginOrNull

internal class FloconGrpcPlugin() {

    fun reportRequest(callId: String, request: FloconNetworkRequest) {
        Flocon.networkPluginOrNull?.logRequest(
            FloconNetworkCallRequest(
                floconCallId = callId,
                floconNetworkType = "grpc",
                request = request,
                isMocked = false,
            )
        )
    }

    fun reportResponse(
        callId: String,
        request: FloconNetworkRequest,
        response: FloconNetworkResponse
    ) {
        val responseTime = System.currentTimeMillis()
        val durationMs = (responseTime - request.startTime).toDouble()

        Flocon.networkPluginOrNull?.logResponse(
            FloconNetworkCallResponse(
                floconCallId = callId,
                floconNetworkType = "grpc",
                response = response,
                durationMs = durationMs,
                isMocked = false,
            )
        )
    }
}

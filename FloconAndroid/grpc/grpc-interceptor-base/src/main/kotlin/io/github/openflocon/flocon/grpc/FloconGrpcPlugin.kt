package io.github.openflocon.flocon.grpc

import io.github.openflocon.flocon.pluginsold.network.model.FloconNetworkRequest
import io.github.openflocon.flocon.pluginsold.network.model.FloconNetworkResponse

internal class FloconGrpcPlugin() {

    fun reportRequest(callId: String, request: FloconNetworkRequest) {
//        FloconApp.instance?.client?.networkPlugin?.logRequest(
//            FloconNetworkCallRequest(
//                floconCallId = callId,
//                floconNetworkType = "grpc",
//                request = request,
//                isMocked = false,
//            )
//        )
    }

    fun reportResponse(
        callId: String,
        request: FloconNetworkRequest,
        response: FloconNetworkResponse
    ) {
        val responseTime = System.currentTimeMillis()
        (responseTime - request.startTime).toDouble()

//        FloconApp.instance?.client?.networkPlugin?.logResponse(
//            FloconNetworkCallResponse(
//                floconCallId = callId,
//                floconNetworkType = "grpc",
//                response = response,
//                durationMs = durationMs,
//                isMocked = false,
//            )
//        )
    }
}

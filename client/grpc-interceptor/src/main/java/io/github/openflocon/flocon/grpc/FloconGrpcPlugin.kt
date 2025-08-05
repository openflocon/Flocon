package io.github.openflocon.flocon.grpc

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkRequest
import java.util.concurrent.ConcurrentHashMap

internal class FloconGrpcPlugin(
    private val floconClient: Flocon.Client? = null,
) {

    private val requests = ConcurrentHashMap<String,FloconNetworkRequest.Request>()

    fun reportRequest(callId: String, request: FloconNetworkRequest.Request) {
        requests[callId] = request
    }

    fun reportResponse(callId: String, response: FloconNetworkRequest.Response) {
        val request = requests[callId] ?: return
        val responseTime = System.currentTimeMillis()
        val durationMs = (responseTime - request.startTime).toDouble()
        val call = FloconNetworkRequest(
            durationMs = durationMs,
            request = request,
            response = response,
            floconNetworkType = "grpc",
        )
        (floconClient ?: Flocon.client)?.send(
            plugin = Protocol.FromDevice.Network.Plugin,
            method = Protocol.FromDevice.Network.Method.LogNetworkCall,
            body = call.toJson().toString(),
        )
        requests.remove(callId)
    }
}

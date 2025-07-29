package io.github.openflocon.flocon.grpc

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.grpc.model.GrpcRequest
import io.github.openflocon.flocon.grpc.model.GrpcResponse

internal class FloconGrpcPlugin(
    private val floconClient: Flocon.Client? = null,
) {

    fun reportRequest(request: GrpcRequest) {
        (floconClient ?: Flocon.client)?.send(
            plugin = Protocol.FromDevice.GRPC.Plugin,
            method = Protocol.FromDevice.GRPC.Method.LogNetworkRequest,
            body = request.toJson().toString(),
        )
    }

    fun reportResponse(response: GrpcResponse) {
        (floconClient ?: Flocon.client)?.send(
            plugin = Protocol.FromDevice.GRPC.Plugin,
            method = Protocol.FromDevice.GRPC.Method.LogNetworkResponse,
            body = response.toJson().toString(),
        )
    }
}

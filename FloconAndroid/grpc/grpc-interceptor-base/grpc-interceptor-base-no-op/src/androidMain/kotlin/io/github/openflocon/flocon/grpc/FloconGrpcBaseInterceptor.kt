package io.github.openflocon.flocon.grpc

import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.ClientInterceptor
import io.grpc.MethodDescriptor

abstract class FloconGrpcBaseInterceptor(
    @Suppress("unused") private val grpcFormatter: FloconGrpcBaseFormatter,
) : ClientInterceptor {

    override fun <ReqT : Any?, RespT : Any?> interceptCall(
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        next: Channel,
    ): ClientCall<ReqT, RespT> = next.newCall(method, callOptions)
}

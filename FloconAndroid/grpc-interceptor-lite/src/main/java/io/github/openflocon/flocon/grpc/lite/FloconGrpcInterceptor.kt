package io.github.openflocon.flocon.grpc.lite

import io.github.openflocon.flocon.grpc.FloconGrpcBaseFormatter
import io.github.openflocon.flocon.grpc.FloconGrpcBaseInterceptor

class FloconGrpcInterceptor : FloconGrpcBaseInterceptor() {

    override val floconGrpcFormatter: FloconGrpcBaseFormatter = FloconGrpcFormatter()
}
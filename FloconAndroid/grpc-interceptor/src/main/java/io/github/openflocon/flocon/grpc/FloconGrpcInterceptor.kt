package io.github.openflocon.flocon.grpc

class FloconGrpcInterceptor : FloconGrpcBaseInterceptor() {

    override val floconGrpcFormatter: FloconGrpcBaseFormatter = FloconGrpcFormatter()
}
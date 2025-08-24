package io.github.openflocon.flocon.grpc

class FloconGrpcInterceptor(
    grpcFormatter: FloconGrpcBaseFormatter = FloconGrpcFormatter()
) : FloconGrpcBaseInterceptor(
    grpcFormatter = grpcFormatter,
)
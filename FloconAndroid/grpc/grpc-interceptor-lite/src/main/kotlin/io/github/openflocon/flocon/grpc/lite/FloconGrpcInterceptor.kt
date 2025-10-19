package io.github.openflocon.flocon.grpc.lite

import io.github.openflocon.flocon.grpc.FloconGrpcBaseInterceptor

class FloconGrpcInterceptor(
    grpcFormatter : FloconGrpcFormatter = FloconGrpcFormatter()
) : FloconGrpcBaseInterceptor(
    grpcFormatter = grpcFormatter
)
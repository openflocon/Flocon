package io.github.openflocon.flocon.myapplication.grpc

import io.github.openflocon.flocon.grpc.lite.FloconGrpcInterceptor
import io.grpc.CallOptions
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.examples.helloworld.GreeterGrpcKt
import io.grpc.examples.helloworld.helloRequest

object GrpcController {
    val channel: ManagedChannel by lazy {
        ManagedChannelBuilder
            .forAddress("localhost", 50051)
            .usePlaintext()
            .intercept(
                FloconGrpcInterceptor()
            )
            .build()
    }

    val greeterClient by lazy {
        GreeterGrpcKt.GreeterCoroutineStub(
            channel = channel,
            callOptions = CallOptions.DEFAULT,
        )
    }

    suspend fun sayHello() : String? {
        try {
            val request = helloRequest {
                name = "florent"
            }
            val response = greeterClient.sayHello(request)
            return response.message
        } catch (t: Throwable) {
            t.printStackTrace()
            return null
        }

    }
}
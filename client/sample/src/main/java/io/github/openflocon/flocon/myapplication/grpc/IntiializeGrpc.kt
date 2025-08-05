package io.github.openflocon.flocon.myapplication.grpc

import io.github.openflocon.flocon.grpc.FloconGrpcInterceptor
import io.grpc.CallOptions
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.examples.helloworld.GreeterGrpcKt
import io.grpc.examples.helloworld.helloRequest
import kotlin.getValue

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

    val geeterClient by lazy {
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
            val response = geeterClient.sayHello(request)
            return response.message
        } catch (t: Throwable) {
            t.printStackTrace()
            return null
        }

    }
}
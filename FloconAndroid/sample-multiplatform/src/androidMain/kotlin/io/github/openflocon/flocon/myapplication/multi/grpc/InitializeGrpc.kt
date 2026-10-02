package io.github.openflocon.flocon.myapplication.multi.grpc

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder

object GrpcController {
    val channel: ManagedChannel by lazy {
        ManagedChannelBuilder
            .forAddress("localhost", 50051)
            .usePlaintext()
//            .intercept(
//                FloconGrpcInterceptor()
//            )
            .build()
    }

//    val greeterClient by lazy {
//        GreeterGrpcKt.GreeterCoroutineStub(
//            channel = channel,
//            callOptions = CallOptions.DEFAULT,
//        )
//    }

    suspend fun sayHello(): String? {
        try {
//            val request = helloRequest {
//                name = "florent"
//            }
//            val response = greeterClient.sayHello(request)
//            return response.message
            TODO()
        } catch (t: Throwable) {
            t.printStackTrace()
            return null
        }

    }
}

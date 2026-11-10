package com.flocon.sample.android.grpc

import com.flocon.sample.android.PostHogAppLogger
import com.posthog.PostHog
import io.github.openflocon.flocon.grpc.lite.FloconGrpcInterceptor
import io.grpc.CallOptions
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.examples.helloworld.GreeterGrpcKt
import io.grpc.examples.helloworld.helloRequest

/**
 * Calls the test server of grpc/grpc-test-server (start.sh), reachable on localhost:50051 with `adb reverse tcp:50051 tcp:50051`
 */
object GrpcController {
    private val channel: ManagedChannel by lazy {
        ManagedChannelBuilder
            .forAddress("localhost", 50051)
            .usePlaintext()
            .intercept(
                FloconGrpcInterceptor()
            )
            .build()
    }

    private val greeterClient by lazy {
        GreeterGrpcKt.GreeterCoroutineStub(
            channel = channel,
            callOptions = CallOptions.DEFAULT,
        )
    }

    suspend fun sayHello(): String? {
        PostHog.capture(
            event = "grpc_hello_requested",
            properties = mapOf("rpc_method" to "say_hello"),
        )
        return try {
            val request = helloRequest {
                name = "florent"
            }
            greeterClient.sayHello(request).message.also {
                PostHogAppLogger.info(
                    message = "grpc hello request completed",
                    attributes = mapOf("rpc_method" to "say_hello"),
                )
            }
        } catch (t: Throwable) {
            PostHogAppLogger.error(
                message = "grpc hello request failed",
                attributes = mapOf(
                    "rpc_method" to "say_hello",
                    "error_type" to t.javaClass.simpleName,
                ),
            )
            t.printStackTrace()
            null
        }
    }
}

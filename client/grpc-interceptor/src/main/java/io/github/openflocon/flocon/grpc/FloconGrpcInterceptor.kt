package io.github.openflocon.flocon.grpc

import io.github.openflocon.flocon.grpc.model.toHeaders
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkRequest
import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.ClientInterceptor
import io.grpc.ForwardingClientCall
import io.grpc.ForwardingClientCallListener
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import io.grpc.Status
import java.util.UUID

private val excluded = setOf(
    "unknownFields",
    "memoizedHashCode",
    "bitField",
    "memoizedSerializedSize",
    "bytes",
)

private val defaultFieldExcluder : (name: String) -> Boolean = { name ->
    var isExcluded = false
    excluded.forEach { toExclude ->
        if (name.startsWith(prefix = toExclude)) {
            isExcluded = true
        }
    }
    isExcluded
}

class FloconGrpcInterceptor(
    private val shouldExcludeField: (name: String) -> Boolean = defaultFieldExcluder,
) : ClientInterceptor {

    private val flipperGrpcPlugin = FloconGrpcPlugin()

    private val gson = buildGsonInstance(shouldExcludeField)

    override fun <ReqT : Any?, RespT : Any?> interceptCall(
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        next: Channel,
    ): ClientCall<ReqT, RespT> {
        val requestId = UUID.randomUUID().toString()
        return LoggingForwardingClientCall(
            floconGrpcPlugin = flipperGrpcPlugin,
            requestId = requestId,
            method = method,
            next = next,
            callOptions = callOptions,
            gson = gson,
        )
    }
}

private class LoggingForwardingClientCall<ReqT, RespT>(
    private val floconGrpcPlugin: FloconGrpcPlugin,
    private val requestId: String,
    private val method: MethodDescriptor<ReqT, RespT>,
    private val next: Channel,
    callOptions: CallOptions,
    private val gson: Gson,
) : ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
    next.newCall(
        method,
        callOptions,
    ),
) {

    private var headers: Metadata? = null

    override fun start(responseListener: Listener<RespT>, headers: Metadata) {
        this.headers = headers
        super.start(
            LoggingClientCallListener(
                floconGrpcPlugin = floconGrpcPlugin,
                requestId = requestId,
                responseListener = responseListener,
                gson = gson
            ),
            headers,
        )
    }

    override fun sendMessage(message: ReqT) {
        super.sendMessage(message)
        floconGrpcPlugin.reportRequest(
            callId = requestId,
            request = FloconNetworkRequest.Request(
                url = next.authority(),
                method = method.fullMethodName,
                body = message?.toJson(gson = gson) ?: "",
                startTime = System.currentTimeMillis(),
                headers = headers?.toHeaders().orEmpty(),
                size = 0, // TODO
            )
        )
    }
}

private class LoggingClientCallListener<RespT>(
    private val floconGrpcPlugin: FloconGrpcPlugin,
    private val requestId: String,
    responseListener: ClientCall.Listener<RespT>,
    private val gson: Gson,
) : ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(
    responseListener,
) {

    private var headers: Metadata? = null
    private var message: RespT? = null

    override fun onClose(status: Status, trailers: Metadata) {
        super.onClose(status, trailers)
        floconGrpcPlugin.reportResponse(
            callId = requestId,
            response = FloconNetworkRequest.Response(
                body = message?.toJson(gson),
                headers = (this.headers ?: trailers).toHeaders(),
                httpCode = null,
                contentType = "grpc",
                size = 0L,
                grpcStatus = status.code.toString(),
            ),
        )
    }

    override fun onMessage(message: RespT) {
        super.onMessage(message)
        this.message = message
    }

    override fun onHeaders(headers: Metadata?) {
        super.onHeaders(headers)
        this.headers = headers
    }
}

private fun buildGsonInstance(
    excluder: (name: String) -> Boolean,
): Gson {
    return GsonBuilder().setPrettyPrinting()
        .setFieldNamingStrategy {
            it.name.removeSuffix("_")
        }
        .setExclusionStrategies(object : ExclusionStrategy {
            override fun shouldSkipField(f: FieldAttributes): Boolean {
                return excluder(f.name)
            }

            override fun shouldSkipClass(clazz: Class<*>): Boolean {
                return false
            }
        }).create()
}

private fun Any.toJson(gson: Gson): String = gson.toJson(this)

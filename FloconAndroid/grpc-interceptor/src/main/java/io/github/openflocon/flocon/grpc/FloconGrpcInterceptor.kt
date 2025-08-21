package io.github.openflocon.flocon.grpc

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.grpc.model.RequestHolder
import io.github.openflocon.flocon.grpc.model.toHeaders
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkRequest
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkResponse
import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.ClientInterceptor
import io.grpc.ForwardingClientCall
import io.grpc.ForwardingClientCallListener
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import io.grpc.Status
import kotlinx.coroutines.runBlocking
import java.util.UUID

private val excluded = setOf(
    "unknownFields",
    "memoizedHashCode",
    "bitField",
    "memoizedSerializedSize",
    "bytes",
)

private val defaultFieldExcluder: (name: String) -> Boolean = { name ->
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
        val networkPlugin = FloconApp.instance?.client?.networkPlugin
        if (networkPlugin == null) {
            // do not intercept if no network plugin, just call
            return next.newCall(method, callOptions)
        }

        val callId = UUID.randomUUID().toString()
        return LoggingForwardingClientCall(
            floconGrpcPlugin = flipperGrpcPlugin,
            callId = callId,
            method = method,
            next = next,
            callOptions = callOptions,
            gson = gson,
        )
    }
}

private class LoggingForwardingClientCall<ReqT, RespT>(
    private val floconGrpcPlugin: FloconGrpcPlugin,
    private val callId: String,
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

    val requestHolder = RequestHolder()

    private var headers: Metadata? = null

    override fun start(responseListener: Listener<RespT>, headers: Metadata) {
        this.headers = headers
        super.start(
            LoggingClientCallListener(
                floconGrpcPlugin = floconGrpcPlugin,
                callId = callId,
                requestHolder = requestHolder,
                responseListener = responseListener,
                gson = gson
            ),
            headers,
        )
    }

    override fun sendMessage(message: ReqT) {
        val request = FloconNetworkRequest(
            url = next.authority(),
            method = method.fullMethodName,
            body = message?.toJson(gson = gson) ?: "",
            startTime = System.currentTimeMillis(),
            headers = headers?.toHeaders().orEmpty(),
            size = 0, // TODO
            isMocked = false, // cannot mock grpc
        )
        requestHolder.request.complete(request)
        floconGrpcPlugin.reportRequest(
            callId = callId,
            request = request
        )
        super.sendMessage(message)
    }
}

private class LoggingClientCallListener<RespT>(
    private val floconGrpcPlugin: FloconGrpcPlugin,
    private val callId: String,
    responseListener: ClientCall.Listener<RespT>,
    private val gson: Gson,
    private val requestHolder: RequestHolder,
) : ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(
    responseListener,
) {

    private var headers: Metadata? = null
    private var message: RespT? = null

    override fun onClose(status: Status, trailers: Metadata) {
        try {
            runBlocking { requestHolder.request.await() }.let { request ->
                floconGrpcPlugin.reportResponse(
                    callId = callId,
                    request = request,
                    response = FloconNetworkResponse(
                        body = message?.toJson(gson),
                        headers = (this.headers ?: trailers).toHeaders(),
                        httpCode = null,
                        contentType = "grpc",
                        size = 0L,
                        grpcStatus = status.code.toString(),
                        error = null,
                    ),
                )
            }
        } catch (t: Throwable) {
            FloconLogger.logError("cannot find request for callId $callId", t)
        }
        super.onClose(status, trailers)
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

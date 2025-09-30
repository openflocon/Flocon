package io.github.openflocon.flocon.grpc

import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.grpc.model.RequestHolder
import io.github.openflocon.flocon.grpc.model.toHeaders
import io.github.openflocon.flocon.plugins.network.FloconNetworkPlugin
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

abstract class FloconGrpcBaseInterceptor(
    private val grpcFormatter: FloconGrpcBaseFormatter,
) : ClientInterceptor {

    private val floconGrpcPlugin = FloconGrpcPlugin()

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
            floconGrpcPlugin = floconGrpcPlugin,
            floconNetworkPlugin = networkPlugin,
            callId = callId,
            method = method,
            next = next,
            formatter = grpcFormatter,
            callOptions = callOptions,
        )
    }
}

private class LoggingForwardingClientCall<ReqT, RespT>(
    private val floconNetworkPlugin: FloconNetworkPlugin,
    private val floconGrpcPlugin: FloconGrpcPlugin,
    private val callId: String,
    private val method: MethodDescriptor<ReqT, RespT>,
    private val next: Channel,
    private val formatter: FloconGrpcBaseFormatter,
    callOptions: CallOptions,
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
                formatter = formatter,
            ),
            headers,
        )
    }

    override fun sendMessage(message: ReqT) {
        val request = FloconNetworkRequest(
            url = next.authority(),
            method = method.fullMethodName,
            body = formatter.format(message),
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
        floconNetworkPlugin.badQualityConfig?.let {
            executeBadQuality(it)
        }
        super.sendMessage(message)
    }
}

private class LoggingClientCallListener<RespT>(
    private val floconGrpcPlugin: FloconGrpcPlugin,
    private val callId: String,
    responseListener: ClientCall.Listener<RespT>,
    private val requestHolder: RequestHolder,
    private val formatter: FloconGrpcBaseFormatter,
) : ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(
    responseListener,
) {

    private var headers: Metadata? = null
    private var message: RespT? = null

    override fun onClose(status: Status, trailers: Metadata) {
        try {
            runBlocking { requestHolder.request.await() }.let { request ->
                status.cause?.let { cause ->
                    floconGrpcPlugin.reportResponse(
                        callId = callId,
                        request = request,
                        response = FloconNetworkResponse(
                            body = null,
                            headers = emptyMap(),
                            httpCode = null,
                            contentType = "grpc",
                            size = null,
                            grpcStatus = null,
                            error = cause.message ?: cause.javaClass.simpleName,
                            requestHeaders = null,
                            isImage = false,
                        ),
                    )
                } ?: run {
                    floconGrpcPlugin.reportResponse(
                        callId = callId,
                        request = request,
                        response = FloconNetworkResponse(
                            body = formatter.format(message),
                            headers = (this.headers ?: trailers).toHeaders(),
                            httpCode = null,
                            contentType = "grpc",
                            size = 0L,
                            grpcStatus = status.code.toString(),
                            error = null,
                            requestHeaders = null,
                            isImage = false,
                        ),
                    )
                }
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
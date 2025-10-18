package io.github.openflocon.flocon.plugins.network.mapper

import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkCallRequest
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkCallResponse
import io.github.openflocon.flocon.plugins.network.model.FloconWebSocketEvent
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

@Serializable
internal class FloconNetworkCallRequestRemote(
    val floconCallId: String,
    val floconNetworkType : String,
    val isMocked : Boolean,

    val url: String,
    val method: String,
    val startTime: Long,
    val requestBody: String?,
    val requestHeaders: Map<String, String>,
    val requestSize: Long?,
)

internal fun FloconNetworkCallRequest.floconNetworkCallRequestToJson(): String {
    val remoteModel = FloconNetworkCallRequestRemote(
        floconCallId = floconCallId,
        floconNetworkType = floconNetworkType,
        isMocked = isMocked,
        url = request.url,
        method = request.method,
        startTime = request.startTime,
        requestBody = request.body,
        requestHeaders = request.headers,
        requestSize = request.size
    )
    return FloconEncoder.json.encodeToString(remoteModel)
}

@Serializable
internal class FloconNetworkCallResponseRemote(
    val floconCallId: String,
    val durationMs: Double,
    val floconNetworkType: String,
    val isMocked: Boolean,
    val responseHttpCode: Int?,
    val responseGrpcStatus: String?,
    val responseContentType: String?,
    val responseBody: String?,
    val responseSize: Long?,
    val responseHeaders: Map<String, String>,
    val requestHeaders: Map<String, String>?, // we might receive the request headers later if the interceptor is at first position in the http interceptor chain
    val responseError: String?,
    val isImage: Boolean,
)

internal fun FloconNetworkCallResponse.floconNetworkCallResponseToJson(): String {
    val remoteModel = FloconNetworkCallResponseRemote(
        floconCallId = floconCallId,
        floconNetworkType = floconNetworkType,
        isMocked = isMocked,
        durationMs = durationMs,
        responseHttpCode = response.httpCode,
        responseGrpcStatus = response.grpcStatus,
        responseContentType = response.contentType,
        responseBody = response.body,
        responseHeaders = response.headers,
        requestHeaders = response.requestHeaders?.takeIf {
            it.isNotEmpty()
        },
        responseSize = response.size,
        isImage = response.isImage,
        responseError = response.error,
    )

    return FloconEncoder.json.encodeToString(remoteModel)
}

@Serializable
internal class FloconWebSocketEventRemote(
    val id: String,
    val event: String,
    val url: String,
    val size: Long,
    val timestamp: Long,
    val message: String?,
    val error: String?,
)

internal fun FloconWebSocketEvent.floconNetworkWebSocketEventToJson(): String {
    val remoteModel = FloconWebSocketEventRemote(
        id = UUID.randomUUID().toString(),
        event = when (event) {
            FloconWebSocketEvent.Event.Closed -> "closed"
            FloconWebSocketEvent.Event.Closing -> "closing"
            FloconWebSocketEvent.Event.Error -> "error"
            FloconWebSocketEvent.Event.ReceiveMessage -> "received"
            FloconWebSocketEvent.Event.SendMessage -> "sent"
            FloconWebSocketEvent.Event.Open -> "open"
        },
        url = websocketUrl,
        size = size,
        timestamp = timeStamp,
        message = message,
        error = error?.message
    )
    return FloconEncoder.json.encodeToString(remoteModel)
}
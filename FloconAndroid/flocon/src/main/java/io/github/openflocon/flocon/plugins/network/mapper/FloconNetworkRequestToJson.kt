package io.github.openflocon.flocon.plugins.network.mapper

import io.github.openflocon.flocon.plugins.network.model.FloconNetworkCallRequest
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkCallResponse
import io.github.openflocon.flocon.plugins.network.model.FloconWebSocketEvent
import org.json.JSONObject
import java.util.UUID

internal fun floconNetworkCallRequestToJson(network: FloconNetworkCallRequest): JSONObject {
    val json = JSONObject()

    with(network) {
        json.put("floconCallId", floconCallId)
        json.put("floconNetworkType", floconNetworkType)
        json.put("isMocked", isMocked)

        json.put("url", request.url)
        json.put("method", request.method)
        json.put("startTime", request.startTime)

        request.body?.let { json.put("requestBody", it) }
        json.put("requestHeaders", JSONObject(request.headers))
        json.put("requestSize", request.size)
    }

    return json
}

fun floconNetworkCallResponseToJson(network: FloconNetworkCallResponse): JSONObject {
    val json = JSONObject()

    with(network) {
        json.put("floconCallId", floconCallId)
        json.put("floconNetworkType", floconNetworkType)
        json.put("isMocked", isMocked)

        json.put("durationMs", durationMs)
        response.httpCode?.let { json.put("responseHttpCode", it) }
        response.grpcStatus?.let { json.put("responseGrpcStatus", it) }
        response.contentType?.let { json.put("responseContentType", it) }
        response.body?.let { json.put("responseBody", it) }
        json.put("responseHeaders", JSONObject(response.headers))
        // if we have delayed headers
        response.requestHeaders?.takeIf { it.isNotEmpty() }?.let { requestHeaders ->
            json.put("requestHeaders", JSONObject(requestHeaders))
        }
        json.put("responseSize", response.size)
        json.put("isImage", response.isImage)
        response.error?.let { json.put("responseError", it) }
    }

    return json
}


internal fun floconNetworkWebSocketEventToJson(
    webSocketEvent: FloconWebSocketEvent,
): JSONObject {
    val json = JSONObject()

    with(webSocketEvent) {
        json.put("id", UUID.randomUUID().toString())
        json.put("event", when(event) {
            FloconWebSocketEvent.Event.Closed -> "closed"
            FloconWebSocketEvent.Event.Closing -> "closing"
            FloconWebSocketEvent.Event.Error -> "error"
            FloconWebSocketEvent.Event.ReceiveMessage -> "receiveMessage"
            FloconWebSocketEvent.Event.SendMessage -> "sendMessage"
            FloconWebSocketEvent.Event.Open -> "open"
        })
        // json.put("isMocked", isMocked)

        json.put("url", websocketUrl)
        json.put("timestamp", webSocketEvent.timeStamp)

        webSocketEvent.message?.let {
            json.put("message", it)
        }
        webSocketEvent.error?.message?.let {
            json.put("error", it)
        }
    }

    return json
}
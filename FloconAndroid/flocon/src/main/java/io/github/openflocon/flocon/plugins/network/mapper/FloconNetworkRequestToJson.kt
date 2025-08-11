package io.github.openflocon.flocon.plugins.network.mapper

import io.github.openflocon.flocon.plugins.network.model.FloconNetworkCall
import org.json.JSONObject


fun floconNetworkCallToJson(network: FloconNetworkCall): JSONObject {
    val json = JSONObject()

    with(network) {
        json.put("floconNetworkType", floconNetworkType)
        json.put("isMocked", isMocked)

        json.put("url", request.url)
        json.put("method", request.method)
        json.put("startTime", request.startTime)
        json.put("durationMs", durationMs)

        request.body?.let { json.put("requestBody", it) }
        json.put("requestHeaders", JSONObject(request.headers))
        json.put("requestSize", request.size)

        response.httpCode?.let { json.put("responseHttpCode", it) }
        response.grpcStatus?.let { json.put("responseGrpcStatus", it) }
        response.contentType?.let { json.put("responseContentType", it) }
        response.body?.let { json.put("responseBody", it) }
        json.put("responseHeaders", JSONObject(response.headers))
        json.put("responseSize", response.size)
    }

    return json
}
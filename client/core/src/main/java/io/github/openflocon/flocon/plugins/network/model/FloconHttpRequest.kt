package io.github.openflocon.flocon.plugins.network.model

import org.json.JSONObject

data class FloconNetworkRequest(
    val request: Request,
    val response: Response,
    val durationMs: Double,
    val floconNetworkType: String,
) {
    data class Request(
        val url: String,
        val method: String,
        val startTime: Long,
        val headers: Map<String, String>,
        val body: String?,
        val size: Long?,
    )

    data class Response(
        val httpCode: Int?,
        val grpcStatus: String?,
        val contentType: String?,
        val body: String?,
        val size: Long?,
        val headers: Map<String, String>,
    )

    fun toJson(): JSONObject {
        val json = JSONObject()

        json.put("floconNetworkType", floconNetworkType)

        json.put("url", request.url)
        json.put("method", request.method)
        json.put("startTime", request.startTime)
        json.put("durationMs", durationMs)

        request.body?.let { json.put("requestBody", it) }
        json.put("requestHeaders", JSONObject(request.headers))
        json.put("requestSize", request.size)

        response.httpCode?.let { json.put("responseHttpCode", it) }
        response.grpcStatus?.let { json.put("responseGrpcStatus", it) }
        response.contentType?.let {  json.put("responseContentType", it) }
        response.body?.let { json.put("responseBody", it) }
        json.put("responseHeaders", JSONObject(response.headers))
        json.put("responseSize", response.size)

        return json
    }
}
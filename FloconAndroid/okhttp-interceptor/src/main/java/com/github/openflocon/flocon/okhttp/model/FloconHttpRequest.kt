package com.github.openflocon.flocon.okhttp.model

import org.json.JSONObject

data class FloconHttpRequest(
    val url: String,
    val method: String,
    val startTime: Long,
    val durationMs: Double,
    // request
    val requestHeaders: Map<String, String>,
    val requestBody: String?,
    val requestSize: Long?,
    // response
    val responseHttpCode: Int,
    val responseContentType: String?,
    val responseBody: String?,
    val responseSize: Long?,
    val responseHeaders: Map<String, String>,
) {
    fun toJson(): JSONObject {
        val json = JSONObject()

        json.put("url", url)
        json.put("method", method)
        json.put("startTime", startTime)
        json.put("durationMs", durationMs)

        requestBody?.let { json.put("requestBody", it) }
        json.put("requestHeaders", JSONObject(requestHeaders))
        json.put("requestSize", requestSize)

        json.put("responseHttpCode", responseHttpCode)
        responseContentType?.let {  json.put("responseContentType", it) }
        responseBody?.let { json.put("responseBody", it) }
        json.put("responseHeaders", JSONObject(responseHeaders))
        json.put("responseSize", responseSize)

        return json
    }
}
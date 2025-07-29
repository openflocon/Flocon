package com.github.openflocon.flocon.grpc.model

import org.json.JSONObject

internal data class GrpcResponse(
    val id: String,
    val unixTimestampMs: Long,
    val status: String,
    val cause: String?,
    val headers: List<GrpcHeader>,
    val data: String?,
) {
    fun toJson() : JSONObject {
        return JSONObject().apply {
            put("id", id)
            put("timestamp", unixTimestampMs)
            put("status", status)
            put("headers", GrpcHeader.Companion.listOfHeaderToJson(headers))
            put("data", data)
            put("cause", cause)
        }
    }
}
package io.github.openflocon.flocon.grpc.model

import org.json.JSONObject

internal data class GrpcRequest(
    val id: String,
    val unixTimestampMs: Long,
    val authority: String,
    val method: String,
    val headers: List<GrpcHeader>,
    val data: String,
) {
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("id", id)
            put("timestamp", unixTimestampMs)
            put("authority", authority)
            put("method", method)
            put("headers", GrpcHeader.Companion.listOfHeaderToJson(headers))
            put("data", data)
        }
    }
}
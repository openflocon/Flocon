package io.github.openflocon.flocon.grpc.model

import io.grpc.Metadata
import org.json.JSONArray
import org.json.JSONObject

internal data class GrpcHeader(
    val key: String,
    val value: String,
) {
    fun toJson() : JSONObject {
        return JSONObject().apply {
            put("key", key)
            put("value", value)
        }
    }
    companion object Companion {
        fun listOfHeaderToJson(headers: List<GrpcHeader>) : JSONArray {
            return JSONArray().apply {
                headers.forEach {
                    put(it.toJson())
                }
            }
        }
    }
}

internal fun Metadata.toHeaders(): List<GrpcHeader> {
    return keys().mapNotNull {
        try {
            GrpcHeader(
                it,
                get(Metadata.Key.of(it, Metadata.ASCII_STRING_MARSHALLER)).toString(),
            )
        } catch (e: Exception) {
            null
        }
    }
}


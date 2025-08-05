package io.github.openflocon.flocon.grpc.model

import io.grpc.Metadata
import org.json.JSONArray
import org.json.JSONObject

internal fun Metadata.toHeaders(): Map<String, String> {
    return keys().mapNotNull {
        try {
            Pair(
                it,
                get(Metadata.Key.of(it, Metadata.ASCII_STRING_MARSHALLER)).toString(),
            )
        } catch (e: Exception) {
            null
        }
    }.toMap()
}


package io.github.openflocon.data.local.network.models

import androidx.room.Embedded

data class FloconHttpRequestEntityLite(
    val uuid: String,
    val deviceId: String, // To associate with a device
    // if it's a graphql method, this item is not null
    @Embedded(prefix = "graphql_")
    val graphql: FloconHttpRequestEntityGraphQlEmbedded?,

    @Embedded(prefix = "http_")
    val http: FloconHttpRequestEntityHttpEmbedded?,

    @Embedded(prefix = "grpc_")
    val grpc: FloconHttpRequestEntityGrpcEmbedded?,

    val url: String,
    val method: String,
    val startTime: Long,
    val durationMs: Double,
    // removed val requestHeaders: Map<String, String>,
    // removed val requestBody: String?,
    // removed val requestByteSize: Long,
    // removed val responseContentType: String?,
    // removed val responseBody: String?,
    // removed val responseHeaders: Map<String, String>,
    // removed val responseByteSize: Long,
)

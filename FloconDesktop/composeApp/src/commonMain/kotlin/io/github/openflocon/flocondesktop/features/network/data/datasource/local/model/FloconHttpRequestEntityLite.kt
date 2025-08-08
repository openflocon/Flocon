package io.github.openflocon.flocondesktop.features.network.data.datasource.local.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

data class FloconHttpRequestEntityLite(
    val uuid: String,
    val deviceId: String, // To associate with a device
    // if it's a graphql method, this item is not null
    @Embedded(prefix = "graphql_")
    val graphql: GraphQlEmbedded?,

    @Embedded(prefix = "http_")
    val http: HttpEmbedded?,

    @Embedded(prefix = "grpc_")
    val grpc: GrpcEmbedded?,

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
) {
    data class GraphQlEmbedded(
        val query: String,
        val operationType: String,
        val isSuccess: Boolean,
        val responseHttpCode: Int,
    )

    data class HttpEmbedded(
        val responseHttpCode: Int,
    )

    data class GrpcEmbedded(
        val responseStatus: String,
    )
}

package io.github.openflocon.flocondesktop.features.network.data.datasource.local.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(value = ["deviceId", "packageName"])
    ]
)
data class FloconHttpRequestEntity(
    @PrimaryKey
    val uuid: String,

    val deviceId: String, // To associate with a device

    val packageName: String,

    @Embedded
    val infos: FloconHttpRequestInfosEntity,
    // if it's a graphql method, this item is not null
    @Embedded(prefix = "graphql_")
    val graphql: GraphQlEmbedded?,

    @Embedded(prefix = "http_")
    val http: HttpEmbedded?,

    @Embedded(prefix = "grpc_")
    val grpc: GrpcEmbedded?,
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

data class FloconHttpRequestInfosEntity(
    val url: String,
    val method: String,
    val startTime: Long,
    val durationMs: Double,
    val requestHeaders: Map<String, String>,
    val requestBody: String?,
    val requestByteSize: Long,
    val responseContentType: String?,
    val responseBody: String?,
    val responseHeaders: Map<String, String>,
    val responseByteSize: Long,
)

package io.github.openflocon.data.local.network.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(value = ["deviceId", "packageName"]),
    ],
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
    val graphql: FloconHttpRequestEntityGraphQlEmbedded?,

    @Embedded(prefix = "http_")
    val http: FloconHttpRequestEntityHttpEmbedded?,

    @Embedded(prefix = "grpc_")
    val grpc: FloconHttpRequestEntityGrpcEmbedded?,
)

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

package io.github.openflocon.data.local.network.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import io.github.openflocon.data.local.network.models.graphql.NetworkCallGraphQlRequestEmbedded
import io.github.openflocon.data.local.network.models.graphql.NetworkCallGraphQlResponseEmbedded
import io.github.openflocon.data.local.network.models.grpc.NetworkCallGrpcResponseEmbedded
import io.github.openflocon.data.local.network.models.http.NetworkCallHttpResponseEmbedded

@Entity(
    indices = [
        Index(value = ["deviceId", "packageName"]),
    ],
)
data class FloconNetworkCallEntity(
    @PrimaryKey
    val callId: String,

    val deviceId: String, // To associate with a device
    val packageName: String,
    val type: FloconNetworkCallType,

    @Embedded(prefix = "request_")
    val request: FloconNetworkRequestEmbedded,

    @Embedded(prefix = "response_")
    val response: FloconNetworkResponseEmbedded?, // null if we did not received yet the response
)

enum class FloconNetworkCallType {
    HTTP,
    GRAPHQL,
    GRPC,
}

data class FloconNetworkRequestEmbedded(
    val url: String,
    val method: String,
    val startTime: Long,
    val requestHeaders: Map<String, String>,
    val requestBody: String?,
    val requestByteSize: Long,
    val isMocked: Boolean,

    @Embedded(prefix = "graphql_")
    val graphql: NetworkCallGraphQlRequestEmbedded?,
)

data class FloconNetworkResponseEmbedded(
    val durationMs: Double,
    val responseContentType: String?,
    val responseBody: String?,
    val responseHeaders: Map<String, String>,
    val responseByteSize: Long,
    val responseError: String?,

    @Embedded(prefix = "graphql_")
    val graphql: NetworkCallGraphQlResponseEmbedded?,

    @Embedded(prefix = "http_")
    val http: NetworkCallHttpResponseEmbedded?,

    @Embedded(prefix = "grpc_")
    val grpc: NetworkCallGrpcResponseEmbedded?,
)

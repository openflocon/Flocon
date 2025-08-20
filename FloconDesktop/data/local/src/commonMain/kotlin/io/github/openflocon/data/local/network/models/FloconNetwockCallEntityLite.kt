package io.github.openflocon.data.local.network.models

import androidx.room.Embedded
import io.github.openflocon.data.local.network.models.graphql.NetworkCallGraphQlRequestEmbedded
import io.github.openflocon.data.local.network.models.graphql.NetworkCallGraphQlResponseEmbedded
import io.github.openflocon.data.local.network.models.grpc.NetworkCallGrpcResponseEmbedded
import io.github.openflocon.data.local.network.models.http.NetworkCallHttpResponseEmbedded

data class FloconNetwockCallEntityLite(
    val callId: String,

    val deviceId: String, // To associate with a device
    val packageName: String,

    val type: FloconNetworkCallType,

    @Embedded(prefix = "request_")
    val request: FloconNetworkRequestLiteEmbedded,

    @Embedded(prefix = "response_")
    val response: FloconNetworkResponseLiteEmbedded?,
)

data class FloconNetworkRequestLiteEmbedded(
    val url: String,
    val method: String,
    val startTime: Long,
    val requestHeaders: Map<String, String>,
    //val requestBody: String?,
    val requestByteSize: Long,
    val isMocked: Boolean,

    @Embedded(prefix = "graphql_")
    val graphql: NetworkCallGraphQlRequestEmbedded?,
)

data class FloconNetworkResponseLiteEmbedded(
    val durationMs: Long,
    val responseError: String?,
    val responseContentType: String?,
    //val responseBody: String?,
    val responseHeaders: Map<String, String>,
    val responseByteSize: Long,

    @Embedded(prefix = "graphql_")
    val graphql: NetworkCallGraphQlResponseEmbedded?,

    @Embedded(prefix = "http_")
    val http: NetworkCallHttpResponseEmbedded?,

    @Embedded(prefix = "grpc_")
    val grpc: NetworkCallGrpcResponseEmbedded?,
)

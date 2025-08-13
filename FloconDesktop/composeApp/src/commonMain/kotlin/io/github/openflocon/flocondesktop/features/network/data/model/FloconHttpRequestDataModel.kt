package io.github.openflocon.flocondesktop.features.network.data.model

import kotlinx.serialization.Serializable

@Serializable
data class FloconNetworkRequestDataModel(
    val floconCallId: String? = null,
    val floconNetworkType: String? = null,

    val url: String? = null,
    val method: String? = null,
    val startTime: Long? = null,
    // request
    val requestHeaders: Map<String, String>? = null,
    val requestBody: String? = null,
    val requestSize: Long? = null,
    val isMocked: Boolean? = null,
)

@Serializable
data class FloconNetworkCallIdDataModel(
    val floconCallId: String? = null,
)

@Serializable
data class FloconNetworkResponseDataModel(
    val floconCallId: String? = null,
    val floconNetworkType: String? = null,

    val durationMs: Double? = null,
    val responseHttpCode: Int? = null, // ex: 200
    val responseContentType: String? = null,
    val responseBody: String? = null,
    val responseHeaders: Map<String, String>? = null,
    val responseSize: Long? = null,
    val responseGrpcStatus: String? = null,
)

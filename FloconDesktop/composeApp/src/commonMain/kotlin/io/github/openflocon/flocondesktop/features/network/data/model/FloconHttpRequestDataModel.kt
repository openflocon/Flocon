package io.github.openflocon.flocondesktop.features.network.data.model

import kotlinx.serialization.Serializable

@Serializable
data class FloconHttpRequestDataModel(
    val url: String? = null,
    val method: String? = null,
    val startTime: Long? = null,
    val durationMs: Double? = null,
    // request
    val requestHeaders: Map<String, String>? = null,
    val requestBody: String? = null,
    val requestSize: Long? = null,
    // response
    val responseHttpCode: Int? = null, // ex: 200
    val responseContentType: String? = null,
    val responseBody: String? = null,
    val responseHeaders: Map<String, String>? = null,
    val responseSize: Long? = null,
)

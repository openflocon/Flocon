package io.github.openflocon.flocon.plugins.network.model

data class FloconNetworkRequest(
    val url: String,
    val method: String,
    val startTime: Long,
    val headers: Map<String, String>,
    val body: String?,
    val size: Long?,
    val isMocked: Boolean,
)

data class FloconNetworkResponse(
    val httpCode: Int?,
    val grpcStatus: String?,
    val contentType: String?,
    val body: String?,
    val size: Long?,
    val headers: Map<String, String>,
    val requestHeaders: Map<String, String>?, // we might receive the request headers later if the interceptor is at first position in the http interceptor chain
    val error: String?,
)
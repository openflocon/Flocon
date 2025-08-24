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
    val error: String?,
)
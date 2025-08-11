package io.github.openflocon.flocon.plugins.network.model

data class FloconNetworkCall(
    val request: FloconNetworkRequest,
    val response: FloconNetworkResponse,
    val durationMs: Double,
    val floconNetworkType: String,
    val isMocked: Boolean,
)

data class FloconNetworkRequest(
    val url: String,
    val method: String,
    val startTime: Long,
    val headers: Map<String, String>,
    val body: String?,
    val size: Long?,
)

data class FloconNetworkResponse(
    val httpCode: Int?,
    val grpcStatus: String?,
    val contentType: String?,
    val body: String?,
    val size: Long?,
    val headers: Map<String, String>,
)
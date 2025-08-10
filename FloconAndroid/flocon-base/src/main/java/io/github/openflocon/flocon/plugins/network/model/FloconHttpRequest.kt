package io.github.openflocon.flocon.plugins.network.model

data class FloconNetworkRequest(
    val request: Request,
    val response: Response,
    val durationMs: Double,
    val floconNetworkType: String,
) {
    data class Request(
        val url: String,
        val method: String,
        val startTime: Long,
        val headers: Map<String, String>,
        val body: String?,
        val size: Long?,
    )

    data class Response(
        val httpCode: Int?,
        val grpcStatus: String?,
        val contentType: String?,
        val body: String?,
        val size: Long?,
        val headers: Map<String, String>,
    )
}
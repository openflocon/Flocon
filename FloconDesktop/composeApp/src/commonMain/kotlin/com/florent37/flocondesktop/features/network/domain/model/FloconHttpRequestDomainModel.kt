package com.florent37.flocondesktop.features.network.domain.model

data class FloconHttpRequestDomainModel(
    val uuid: String,
    val url: String,
    val durationMs: Double,
    val request: Request,
    val response: Response,

) {
    data class Request(
        val startTime: Long,
        val method: String,
        val headers: Map<String, String>,
        val body: String?,
        val byteSize: Long,
    )

    data class Response(
        val httpCode: Int, // ex: 200
        val contentType: String? = null,
        val body: String? = null,
        val headers: Map<String, String>,
        val byteSize: Long,
    )
}

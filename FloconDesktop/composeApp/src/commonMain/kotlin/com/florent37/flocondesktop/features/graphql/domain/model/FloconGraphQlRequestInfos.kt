package com.florent37.flocondesktop.features.graphql.domain.model

data class FloconGraphQlRequestInfos(
    val url: String,
    val durationMs: Double,
    val request: Request,
    val response: Response,
) {
    data class Request(
        val method: String,
        val startTime: Long,
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

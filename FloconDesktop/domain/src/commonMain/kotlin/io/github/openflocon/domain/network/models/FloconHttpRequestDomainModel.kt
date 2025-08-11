package io.github.openflocon.domain.network.models

data class FloconHttpRequestDomainModel(
    val uuid: String,
    val url: String,
    val durationMs: Double,
    val request: Request,
    val response: Response,
    val type: Type,
) {
    data class Request(
        val startTime: Long,
        val method: String,
        val headers: Map<String, String>,
        val body: String?,
        val byteSize: Long,
    )

    data class Response(
        val contentType: String? = null,
        val body: String? = null,
        val headers: Map<String, String>,
        val byteSize: Long,
    )

    sealed interface Type {
        data class GraphQl(
            val httpCode: Int, // ex: 200
            val query: String,
            val operationType: String,
            val isSuccess: Boolean,
        ) : Type
        data class Http(
            val httpCode: Int, // ex: 200
        ) : Type
        data class Grpc(
            val responseStatus: String,
        ) : Type
    }
}

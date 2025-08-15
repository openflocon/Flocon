package io.github.openflocon.domain.network.models

sealed interface FloconNetworkCallDomainModel {
    val callId: String
    val networkRequest: FloconNetworkRequestDomainModel
    val networkResponse: FloconNetworkResponseDomainModel?

    data class Http(
        override val callId: String,
        override val networkRequest: FloconNetworkRequestDomainModel,
        val response: Response?,
    ) : FloconNetworkCallDomainModel {
        data class Response(
            val networkResponse: FloconNetworkResponseDomainModel,
            val httpCode: Int, // ex: 200
        )

        override val networkResponse = response?.networkResponse
    }

    data class GraphQl(
        override val callId: String,
        val request: Request,
        val response: Response?,
    ) : FloconNetworkCallDomainModel {

        data class Request(
            val networkRequest: FloconNetworkRequestDomainModel,
            val query: String,
            val operationType: String,
        )

        data class Response(
            val httpCode: Int, // ex: 200
            val isSuccess: Boolean,
            val networkResponse: FloconNetworkResponseDomainModel,
        )

        override val networkRequest = request.networkRequest
        override val networkResponse = response?.networkResponse
    }

    data class Grpc(
        override val callId: String,
        override val networkRequest: FloconNetworkRequestDomainModel,
        val response: Response?,
    ) : FloconNetworkCallDomainModel {
        data class Response(
            val networkResponse: FloconNetworkResponseDomainModel,
            val responseStatus: String,
        )

        override val networkResponse = response?.networkResponse
    }

}

fun FloconNetworkCallDomainModel.httpCode(): Int? {
    return when (this) {
        is FloconNetworkCallDomainModel.Http -> this.response?.httpCode
        is FloconNetworkCallDomainModel.GraphQl -> this.response?.httpCode
        else -> null
    }
}

data class FloconNetworkCallIdDomainModel(
    val floconCallId: String,
)

data class FloconNetworkRequestDomainModel(
    val url: String,
    val startTime: Long,
    val method: String,
    val headers: Map<String, String>,
    val body: String?,
    val byteSize: Long,
    val isMocked: Boolean,
)

data class FloconNetworkResponseDomainModel(
    val httpCode: Int? = null, // ex: 200
    val contentType: String? = null,
    val body: String? = null,
    val headers: Map<String, String>,
    val byteSize: Long,
    val durationMs: Double
)


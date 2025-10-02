package io.github.openflocon.domain.network.models

import io.github.openflocon.domain.device.models.AppInstance

data class FloconNetworkCallDomainModel(
    val callId: String,
    val appInstance: AppInstance,
    val request: Request,
    val response: Response?,
) {

    data class Request(
        val url: String,
        val startTime: Long,
        val startTimeFormatted: String,
        val method: String,
        val methodFormatted: String,
        val headers: Map<String, String>,
        val body: String?,
        val byteSize: Long,
        val byteSizeFormatted: String,
        val isMocked: Boolean,
        val specificInfos: SpecificInfos,
        val domainFormatted: String, // extracted from url
        val queryFormatted: String, // extracted from url
    ) {
        sealed interface SpecificInfos {
            data object Http: SpecificInfos
            data class GraphQl(
                val query: String,
                val operationType: String,
            ) : SpecificInfos
            data object Grpc : SpecificInfos
        }
    }

    sealed interface Response {

        val durationMs: Double
        val durationFormatted: String
        val statusFormatted: String // extracted from response

        data class Success(
            override val durationMs: Double,
            override val durationFormatted: String,
            val contentType: String? = null,
            val body: String? = null,
            val headers: Map<String, String>,
            val byteSize: Long,
            val byteSizeFormatted: String,
            val specificInfos: SpecificInfos,
            val isImage: Boolean,
            override val statusFormatted: String, // extracted from response
        ) : Response {
            sealed interface SpecificInfos {
                data class Http(
                    val httpCode: Int, // ex: 200
                ) : SpecificInfos
                data class GraphQl(
                    val httpCode: Int, // ex: 200
                    val isSuccess: Boolean,
                ) : SpecificInfos
                data class Grpc(
                    val grpcStatus: String,
                ) : SpecificInfos
            }
        }
        data class Failure(
            override val durationMs: Double,
            override val durationFormatted: String,
            val issue: String,
            override val statusFormatted: String, // extracted from response
        ) : Response
    }
}

fun FloconNetworkCallDomainModel.Response.Success.SpecificInfos.httpCode() : Int? = when(this) {
    is FloconNetworkCallDomainModel.Response.Success.SpecificInfos.GraphQl -> httpCode
    is FloconNetworkCallDomainModel.Response.Success.SpecificInfos.Http -> httpCode
    is FloconNetworkCallDomainModel.Response.Success.SpecificInfos.Grpc -> null
}

fun FloconNetworkCallDomainModel.httpCode(): Int? {
    return when(this.response) {
        is FloconNetworkCallDomainModel.Response.Failure -> null
        is FloconNetworkCallDomainModel.Response.Success -> this.response.specificInfos.httpCode()
        null -> null
    }
}

fun FloconNetworkCallDomainModel.Response.isImage(): Boolean {
    return when(this) {
        is FloconNetworkCallDomainModel.Response.Failure -> false
        is FloconNetworkCallDomainModel.Response.Success -> this.isImage
        null -> false
    }
}

fun FloconNetworkCallDomainModel.byteSize(): Long? {
    return when(this.response) {
        is FloconNetworkCallDomainModel.Response.Failure -> null
        is FloconNetworkCallDomainModel.Response.Success -> this.response.byteSize
        null -> null
    }
}

fun FloconNetworkCallDomainModel.responseByteSizeFormatted(): String? {
    return when(this.response) {
        is FloconNetworkCallDomainModel.Response.Failure -> null
        is FloconNetworkCallDomainModel.Response.Success -> this.response.byteSizeFormatted
        null -> null
    }
}

fun FloconNetworkCallDomainModel.Response.getContentType(): String? {
    return when(this) {
        is FloconNetworkCallDomainModel.Response.Failure -> null
        is FloconNetworkCallDomainModel.Response.Success -> this.contentType
    }
}

data class FloconNetworkCallIdDomainModel(
    val floconCallId: String,
)

data class FloconNetworkResponseOnlyDomainModel(
    val floconCallId: String,
    val toUpdateRequestHeaders: Map<String, String>?,
    val response: FloconNetworkCallDomainModel.Response,
)

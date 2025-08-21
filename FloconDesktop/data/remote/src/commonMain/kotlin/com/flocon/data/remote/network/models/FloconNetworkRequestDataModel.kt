package com.flocon.data.remote.network.models

import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallIdDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkResponseOnlyDomainModel
import kotlinx.serialization.Serializable

@Serializable
data class FloconNetworkRequestDataModel(
    val floconCallId: String? = null,
    val floconNetworkType: String? = null,

    val url: String? = null,
    val method: String? = null,
    val startTime: Long? = null,
    // request
    val requestHeaders: Map<String, String>? = null,
    val requestBody: String? = null,
    val requestSize: Long? = null,
    val isMocked: Boolean? = null,
)

@Serializable
data class FloconNetworkCallIdDataModel(
    val floconCallId: String? = null,
)

@Serializable
data class FloconNetworkResponseDataModel(
    val floconCallId: String? = null,
    val floconNetworkType: String? = null,

    val durationMs: Double? = null,
    val responseHttpCode: Int? = null, // ex: 200
    val responseContentType: String? = null,
    val responseBody: String? = null,
    val responseHeaders: Map<String, String>? = null,
    val responseSize: Long? = null,
    val responseGrpcStatus: String? = null,
    val responseError: String? = null,
)

internal fun FloconNetworkResponseDataModel.toDomain(): FloconNetworkResponseOnlyDomainModel? {
    return try {
        val callId = floconCallId!!
        val durationMs = durationMs ?: 0.0

        val response = if (responseError != null) {
            FloconNetworkCallDomainModel.Response.Failure(
                durationMs = durationMs,
                issue = responseError,
            )
        } else {
            FloconNetworkCallDomainModel.Response.Success(
                durationMs = durationMs,
                contentType = responseContentType,
                body = responseBody,
                headers = responseHeaders.orEmpty(),
                byteSize = responseSize ?: 0L,
                specificInfos = when (floconNetworkType) {
                    "grpc" -> FloconNetworkCallDomainModel.Response.Success.SpecificInfos.Grpc(
                        grpcStatus = responseGrpcStatus!!,
                    )
                    // otherwise tread like http
                    else -> FloconNetworkCallDomainModel.Response.Success.SpecificInfos.Http(
                        httpCode = responseHttpCode!!,
                    )
                }
            )
        }
        FloconNetworkResponseOnlyDomainModel(
            floconCallId = callId,
            response = response,
        )
    } catch (t: Throwable) {
        t.printStackTrace()
        return null
    }
}

internal fun FloconNetworkCallIdDataModel.toDomain(): FloconNetworkCallIdDomainModel? {
    return FloconNetworkCallIdDomainModel(
        floconCallId = floconCallId ?: return null,
    )
}

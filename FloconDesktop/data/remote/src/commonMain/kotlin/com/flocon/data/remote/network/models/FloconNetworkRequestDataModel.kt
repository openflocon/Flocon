package com.flocon.data.remote.network.models

import io.github.openflocon.domain.network.models.FloconNetworkCallIdDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkResponseDomainModel
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
)

internal fun FloconNetworkResponseDataModel.toDomain(): FloconNetworkResponseOnlyDomainModel? {
    return try {
        val callId = floconCallId!!
        val networkResponse = FloconNetworkResponseDomainModel(
            durationMs = durationMs ?: 0.0,
            body = responseBody,
            byteSize = responseSize ?: 0L,
            headers = responseHeaders.orEmpty(),
            contentType = responseContentType,
        )
        when (floconNetworkType) {
            "grpc" -> FloconNetworkResponseOnlyDomainModel.Grpc(
                floconCallId = callId,
                networkResponse = networkResponse,
                grpcStatus = responseGrpcStatus!!,
            )
            // otherwise tread like http
            else -> FloconNetworkResponseOnlyDomainModel.Http(
                floconCallId = callId,
                networkResponse = networkResponse,
                httpCode = responseHttpCode!!,
            )
        }
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

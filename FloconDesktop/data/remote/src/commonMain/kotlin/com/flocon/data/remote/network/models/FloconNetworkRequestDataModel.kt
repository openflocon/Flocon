package com.flocon.data.remote.network.models

import io.github.openflocon.domain.network.models.FloconNetworkCallIdDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkRequestDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkResponseDomainModel
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

internal fun FloconNetworkRequestDataModel.toDomain(): FloconNetworkRequestDomainModel? {
    return FloconNetworkRequestDomainModel(
        body = requestBody,
        headers = requestHeaders.orEmpty(),
        byteSize = requestSize ?: 0L,
        startTime = startTime ?: return null,
        isMocked = isMocked ?: false,
        url = url.orEmpty(),
        method = method.orEmpty()
    )
}

internal fun FloconNetworkResponseDataModel.toDomain() = FloconNetworkResponseDomainModel(
    durationMs = durationMs ?: 0.0,
    body = responseBody,
    byteSize = responseSize ?: 0L,
    headers = responseHeaders.orEmpty()
)

internal fun FloconNetworkCallIdDataModel.toDomain(): FloconNetworkCallIdDomainModel? {
    return FloconNetworkCallIdDomainModel(
        floconCallId = floconCallId ?: return null
    )
}

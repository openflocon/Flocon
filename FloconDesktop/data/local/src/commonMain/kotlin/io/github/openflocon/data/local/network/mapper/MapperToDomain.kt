package io.github.openflocon.data.local.network.mapper

import io.github.openflocon.data.local.network.models.FloconNetworkCallEntity
import io.github.openflocon.data.local.network.models.FloconNetworkCallType
import io.github.openflocon.data.local.network.models.FloconNetworkRequestEmbedded
import io.github.openflocon.data.local.network.models.FloconNetworkResponseEmbedded
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkRequestDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkResponseDomainModel

fun FloconNetworkCallEntity.toDomainModel(): FloconNetworkCallDomainModel? {
    return try {
        when (type) {
            FloconNetworkCallType.HTTP -> FloconNetworkCallDomainModel.Http(
                callId = callId,
                networkRequest = FloconNetworkRequestDomainModel(
                    byteSize = request.requestByteSize,
                    method = request.method,
                    headers = request.requestHeaders,
                    url = request.url,
                    isMocked = request.isMocked,
                    startTime = request.startTime,
                    body = request.requestBody
                ),
                response = response?.let {
                    FloconNetworkCallDomainModel.Http.Response(
                        httpCode = response.http!!.responseHttpCode,
                        networkResponse = toDomainModel(response),
                    )
                }
            )

            FloconNetworkCallType.GRAPHQL -> FloconNetworkCallDomainModel.GraphQl(
                callId = callId,
                request = FloconNetworkCallDomainModel.GraphQl.Request(
                    networkRequest = FloconNetworkRequestDomainModel(
                        byteSize = request.requestByteSize,
                        method = request.method,
                        headers = request.requestHeaders,
                        url = request.url,
                        isMocked = request.isMocked,
                        startTime = request.startTime,
                        body = request.requestBody
                    ),
                    query = request.graphql!!.query,
                    operationType = request.graphql.operationType,
                ),
                response = response?.let {
                    FloconNetworkCallDomainModel.GraphQl.Response(
                        httpCode = response.graphql!!.responseHttpCode,
                        isSuccess = response.graphql.isSuccess,
                        networkResponse = toDomainModel(response),
                    )
                }
            )

            FloconNetworkCallType.GRPC -> FloconNetworkCallDomainModel.Grpc(
                callId = callId,
                networkRequest = FloconNetworkRequestDomainModel(
                    byteSize = request.requestByteSize,
                    method = request.method,
                    headers = request.requestHeaders,
                    url = request.url,
                    isMocked = request.isMocked,
                    startTime = request.startTime,
                    body = request.requestBody
                ),
                response = response?.let {
                    FloconNetworkCallDomainModel.Grpc.Response(
                        networkResponse = toDomainModel(response),
                        responseStatus = response.grpc!!.responseStatus,
                    )
                }
            )
        }
    } catch (t: Throwable) {
        t.printStackTrace()
        return null
    }
}

private fun toDomainModel(response: FloconNetworkResponseEmbedded): FloconNetworkResponseDomainModel =
    with(response) {
        FloconNetworkResponseDomainModel(
            contentType = responseContentType,
            body = responseBody,
            headers = responseHeaders,
            byteSize = responseByteSize,
            durationMs = durationMs,
        )
    }

package io.github.openflocon.data.local.network.mapper

import io.github.openflocon.data.local.network.models.FloconNetworkCallEntity
import io.github.openflocon.data.local.network.models.FloconNetworkCallType
import io.github.openflocon.data.local.network.models.FloconNetworkRequestEmbedded
import io.github.openflocon.data.local.network.models.FloconNetworkResponseEmbedded
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkRequestDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkResponseDomainModel

private fun toDomainModel(request: FloconNetworkRequestEmbedded): FloconNetworkRequestDomainModel {
    return with(request) {
        FloconNetworkRequestDomainModel(
            url = this.url,
            method = this.method,
            startTime = this.startTime,
            headers = this.requestHeaders,
            body = this.requestBody,
            byteSize = this.requestByteSize,
            isMocked = this.isMocked,
        )
    }
}


fun FloconNetworkCallEntity.toDomainModel(): FloconNetworkCallDomainModel? {
    return try {
        val networkRequest = toDomainModel(request)
        when (type) {
            FloconNetworkCallType.HTTP -> FloconNetworkCallDomainModel.Http(
                callId = callId,
                networkRequest = networkRequest,
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
                    networkRequest = networkRequest,
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
                networkRequest = networkRequest,
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

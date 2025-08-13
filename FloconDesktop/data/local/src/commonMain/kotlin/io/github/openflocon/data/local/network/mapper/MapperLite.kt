package io.github.openflocon.data.local.network.mapper

import io.github.openflocon.data.local.network.models.FloconNetwockCallEntityLite
import io.github.openflocon.data.local.network.models.FloconNetworkCallType
import io.github.openflocon.data.local.network.models.FloconNetworkRequestLiteEmbedded
import io.github.openflocon.data.local.network.models.FloconNetworkResponseLiteEmbedded
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkRequestDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkResponseDomainModel
import kotlin.Boolean

fun toDomainModel(request: FloconNetworkRequestLiteEmbedded): FloconNetworkRequestDomainModel {
    return with(request) {
        FloconNetworkRequestDomainModel(
            url = this.url,
            method = this.method,
            startTime = this.startTime,
            headers = this.requestHeaders,
            body = null, // null on lite
            byteSize = this.requestByteSize,
            isMocked = this.isMocked,
        )
    }
}

fun FloconNetwockCallEntityLite.toDomainModel(): FloconNetworkCallDomainModel? {
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

private fun toDomainModel(response: FloconNetworkResponseLiteEmbedded): FloconNetworkResponseDomainModel = with(response) {
    FloconNetworkResponseDomainModel(
        contentType = responseContentType,
        body = null, // null on lite
        headers = responseHeaders,
        byteSize = responseByteSize,
        durationMs = durationMs,
    )
}

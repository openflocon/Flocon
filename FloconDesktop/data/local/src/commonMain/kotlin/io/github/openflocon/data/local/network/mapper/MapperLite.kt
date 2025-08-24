package io.github.openflocon.data.local.network.mapper

import io.github.openflocon.data.local.network.models.FloconNetwockCallEntityLite
import io.github.openflocon.data.local.network.models.FloconNetworkCallType
import io.github.openflocon.data.local.network.models.FloconNetworkResponseLiteEmbedded
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel

fun FloconNetwockCallEntityLite.toDomainModel(): FloconNetworkCallDomainModel? {
    return try {
        FloconNetworkCallDomainModel(
            callId = callId,
            appInstance = appInstance,
            request = toRequestDomainModel(),
            response = response?.toDomainModel(),
        )
    } catch (t: Throwable) {
        t.printStackTrace()
        return null
    }
}

private fun FloconNetwockCallEntityLite.toRequestDomainModel(): FloconNetworkCallDomainModel.Request =
    FloconNetworkCallDomainModel.Request(
        url = request.url,
        method = request.method,
        startTime = request.startTime,
        headers = request.requestHeaders,
        body = null, // null on lite
        byteSize = request.requestByteSize,
        isMocked = request.isMocked,
        specificInfos = when (type) {
            FloconNetworkCallType.HTTP -> FloconNetworkCallDomainModel.Request.SpecificInfos.Http
            FloconNetworkCallType.GRAPHQL -> FloconNetworkCallDomainModel.Request.SpecificInfos.GraphQl(
                query = request.graphql!!.query,
                operationType = request.graphql.operationType,
            )

            FloconNetworkCallType.GRPC -> FloconNetworkCallDomainModel.Request.SpecificInfos.Grpc
        },
    )

private fun FloconNetworkResponseLiteEmbedded.toDomainModel(): FloconNetworkCallDomainModel.Response? {
    return if(responseError != null) {
        FloconNetworkCallDomainModel.Response.Failure(
            durationMs = durationMs,
            responseError
        )
    } else {
        FloconNetworkCallDomainModel.Response.Success(
            contentType = responseContentType,
            body = null, // null on lite
            headers = responseHeaders,
            byteSize = responseByteSize,
            durationMs = durationMs,
            specificInfos = when {
                graphql != null -> FloconNetworkCallDomainModel.Response.Success.SpecificInfos.GraphQl(
                    httpCode = graphql.responseHttpCode,
                    isSuccess = graphql.isSuccess,
                )
                grpc != null -> FloconNetworkCallDomainModel.Response.Success.SpecificInfos.Grpc(
                    grpcStatus = grpc.responseStatus,
                )
                http != null -> FloconNetworkCallDomainModel.Response.Success.SpecificInfos.Http(
                    httpCode = http.responseHttpCode,
                )
                else -> return null
            }
        )
    }
}

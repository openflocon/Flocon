package io.github.openflocon.data.local.network.mapper

import io.github.openflocon.data.local.network.models.FloconNetworkCallEntity
import io.github.openflocon.data.local.network.models.FloconNetworkCallType
import io.github.openflocon.data.local.network.models.FloconNetworkResponseEmbedded
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel


fun FloconNetworkCallEntity.toDomainModel(): FloconNetworkCallDomainModel? {
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

private fun FloconNetworkCallEntity.toRequestDomainModel(): FloconNetworkCallDomainModel.Request =
    FloconNetworkCallDomainModel.Request(
        url = request.url,
        method = request.method,
        startTime = request.startTime,
        headers = request.requestHeaders,
        body = request.requestBody,
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


private fun FloconNetworkResponseEmbedded.toDomainModel(): FloconNetworkCallDomainModel.Response? {
    return if(responseError != null) {
        FloconNetworkCallDomainModel.Response.Failure(
            durationMs = durationMs,
            issue = responseError
        )
    } else {
        FloconNetworkCallDomainModel.Response.Success(
            contentType = responseContentType,
            body = responseBody,
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

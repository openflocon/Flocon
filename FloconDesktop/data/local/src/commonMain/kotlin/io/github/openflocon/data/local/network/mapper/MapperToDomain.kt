package io.github.openflocon.data.local.network.mapper

import io.github.openflocon.data.local.network.models.FloconNetworkCallEntity
import io.github.openflocon.data.local.network.models.FloconNetworkCallType
import io.github.openflocon.data.local.network.models.FloconNetworkResponseEmbedded
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel

fun FloconNetworkCallEntity.toDomainModel(): FloconNetworkCallDomainModel = FloconNetworkCallDomainModel(
    callId = callId,
    appInstance = appInstance,
    request = toRequestDomainModel(),
    response = response?.toDomainModel(),
    isReplayed = isReplayed,
)

private fun FloconNetworkCallEntity.toRequestDomainModel(): FloconNetworkCallDomainModel.Request = FloconNetworkCallDomainModel.Request(
    url = request.url,
    method = request.method,
    startTime = request.startTime,
    headers = request.requestHeaders,
    body = request.requestBody,
    byteSize = request.requestByteSize,
    isMocked = request.isMocked,
    startTimeFormatted = request.startTimeFormatted,
    byteSizeFormatted = request.byteSizeFormatted,
    domainFormatted = request.domainFormatted,
    queryFormatted = request.queryFormatted,
    methodFormatted = request.methodFormatted,
    specificInfos = when (type) {
        FloconNetworkCallType.HTTP -> FloconNetworkCallDomainModel.Request.SpecificInfos.Http
        FloconNetworkCallType.GRAPHQL -> {
            request.graphql?.let { requestGraphQl ->
                FloconNetworkCallDomainModel.Request.SpecificInfos.GraphQl(
                    query = requestGraphQl.query,
                    operationType = requestGraphQl.operationType,
                )
            } ?: FloconNetworkCallDomainModel.Request.SpecificInfos.Http
        }

        FloconNetworkCallType.GRPC -> FloconNetworkCallDomainModel.Request.SpecificInfos.Grpc
        FloconNetworkCallType.WEBSOCKET -> FloconNetworkCallDomainModel.Request.SpecificInfos.WebSocket(
            event = request.websocket?.event ?: "unknown",
        )
    },
)

private fun FloconNetworkResponseEmbedded.toDomainModel(): FloconNetworkCallDomainModel.Response? {
    return if (responseError != null) {
        FloconNetworkCallDomainModel.Response.Failure(
            durationMs = durationMs,
            issue = responseError,
            durationFormatted = durationFormatted,
            statusFormatted = statusFormatted,
        )
    } else {
        FloconNetworkCallDomainModel.Response.Success(
            contentType = responseContentType,
            body = responseBody,
            headers = responseHeaders,
            byteSize = responseByteSize,
            durationMs = durationMs,
            durationFormatted = durationFormatted,
            byteSizeFormatted = responseByteSizeFormatted ?: "",
            isImage = isImage,
            statusFormatted = statusFormatted,
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

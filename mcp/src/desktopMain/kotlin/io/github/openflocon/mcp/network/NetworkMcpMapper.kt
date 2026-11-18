package io.github.openflocon.mcp.network

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel.Request
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel.Response
import io.github.openflocon.domain.network.models.byteSize
import io.github.openflocon.domain.network.models.httpCode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
internal enum class NetworkCallType(val value: String) {
    @SerialName("http")
    Http("http"),

    @SerialName("graphql")
    GraphQl("graphql"),

    @SerialName("grpc")
    Grpc("grpc"),

    @SerialName("websocket")
    WebSocket("websocket"),
}

internal enum class NetworkCallState(val value: String) {
    Pending("pending"),
    Success("success"),
    Error("error"),
    Exception("exception"),
}

internal fun FloconNetworkCallDomainModel.type(): NetworkCallType = when (request.specificInfos) {
    is Request.SpecificInfos.GraphQl -> NetworkCallType.GraphQl
    Request.SpecificInfos.Grpc -> NetworkCallType.Grpc
    Request.SpecificInfos.Http -> NetworkCallType.Http
    is Request.SpecificInfos.WebSocket -> NetworkCallType.WebSocket
}

/**
 * Same rules as the Flocon network screen
 */
internal fun FloconNetworkCallDomainModel.state(): NetworkCallState = when (val response = response) {
    null -> when (val infos = request.specificInfos) {
        is Request.SpecificInfos.WebSocket -> if (infos.event == "error") NetworkCallState.Error else NetworkCallState.Success
        else -> NetworkCallState.Pending
    }

    is Response.Failure -> NetworkCallState.Exception

    is Response.Success -> {
        val isSuccess = when (val infos = response.specificInfos) {
            is Response.Success.SpecificInfos.GraphQl -> infos.isSuccess
            is Response.Success.SpecificInfos.Grpc -> infos.grpcStatus == "OK"
            is Response.Success.SpecificInfos.Http -> infos.httpCode in 200..399
        }
        if (isSuccess) NetworkCallState.Success else NetworkCallState.Error
    }
}

internal fun DeviceIdAndPackageNameDomainModel.toMcpModel() = NetworkTargetMcpModel(
    deviceId = deviceId,
    packageName = packageName,
    currentSessionId = appInstance,
)

internal fun FloconNetworkCallDomainModel.toSummaryMcpModel(
    currentSessionId: Long,
): NetworkCallSummaryMcpModel {
    val state = state()
    return NetworkCallSummaryMcpModel(
        callId = callId,
        type = type().value,
        method = request.method,
        url = request.url,
        startTime = Instant.fromEpochMilliseconds(request.startTime).toString(),
        startTimeMs = request.startTime,
        state = state.value,
        status = response?.statusFormatted ?: (request.specificInfos as? Request.SpecificInfos.WebSocket)?.event,
        httpCode = httpCode(),
        durationMs = response?.durationMs,
        requestSize = request.byteSize,
        responseSize = byteSize(),
        graphQlOperation = (request.specificInfos as? Request.SpecificInfos.GraphQl)?.operationType,
        isMocked = request.isMocked,
        isReplayed = isReplayed,
        error = (response as? Response.Failure)?.issue,
        sessionId = appInstance,
        isCurrentSession = appInstance == currentSessionId,
    )
}

internal fun FloconNetworkCallDomainModel.toDetailMcpModel(
    target: DeviceIdAndPackageNameDomainModel,
    maxBodyChars: Int,
    curl: String?,
) = NetworkCallDetailMcpModel(
    deviceId = target.deviceId,
    packageName = target.packageName,
    summary = toSummaryMcpModel(currentSessionId = target.appInstance),
    request = NetworkRequestMcpModel(
        headers = request.headers,
        body = request.body.toBodyMcpModel(maxBodyChars),
        graphQl = (request.specificInfos as? Request.SpecificInfos.GraphQl)?.let {
            GraphQlRequestMcpModel(operationType = it.operationType, query = it.query)
        },
        webSocketEvent = (request.specificInfos as? Request.SpecificInfos.WebSocket)?.event,
    ),
    response = when (val response = response) {
        null -> null

        is Response.Failure -> NetworkResponseMcpModel(
            contentType = null,
            headers = null,
            body = null,
            isImage = false,
            grpcStatus = null,
            graphQlSuccess = null,
            error = response.issue,
        )

        is Response.Success -> NetworkResponseMcpModel(
            contentType = response.contentType,
            headers = response.headers,
            body = if (response.isImage) null else response.body.toBodyMcpModel(maxBodyChars),
            isImage = response.isImage,
            grpcStatus = (response.specificInfos as? Response.Success.SpecificInfos.Grpc)?.grpcStatus,
            graphQlSuccess = (response.specificInfos as? Response.Success.SpecificInfos.GraphQl)?.isSuccess,
            error = null,
        )
    },
    curl = curl,
)

internal fun String?.toBodyMcpModel(maxChars: Int): NetworkBodyMcpModel? {
    if (isNullOrEmpty()) return null
    val truncated = length > maxChars
    return NetworkBodyMcpModel(
        content = if (truncated) take(maxChars) else this,
        truncated = truncated,
        totalLength = length,
    )
}

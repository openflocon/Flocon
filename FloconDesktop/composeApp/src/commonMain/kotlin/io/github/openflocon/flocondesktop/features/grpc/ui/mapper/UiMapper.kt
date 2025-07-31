package io.github.openflocon.flocondesktop.features.grpc.ui.mapper

import io.github.openflocon.flocondesktop.features.grpc.domain.model.GrpcCallDomainModel
import io.github.openflocon.flocondesktop.features.grpc.domain.model.GrpcHeaderDomainModel
import io.github.openflocon.flocondesktop.features.grpc.domain.model.GrpcResponseDomainModel
import io.github.openflocon.flocondesktop.features.grpc.ui.model.GrpcDetailViewState
import io.github.openflocon.flocondesktop.features.grpc.ui.model.GrpcItemViewState
import io.github.openflocon.flocondesktop.features.network.ui.mapper.formatDuration
import io.github.openflocon.flocondesktop.features.network.ui.mapper.formatTimestamp
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkDetailHeaderUi

fun toUi(domainModel: GrpcCallDomainModel): GrpcItemViewState = GrpcItemViewState(
    callId = domainModel.id,
    method = domainModel.request.method,
    url = domainModel.request.authority,
    status = if (domainModel.response == null) {
        GrpcItemViewState.StatusViewState.Waiting(text = "Waiting")
    } else {
        when (domainModel.response.result) {
            is GrpcResponseDomainModel.CallResult.Error ->
                GrpcItemViewState.StatusViewState.Failure(domainModel.response.result.cause)

            is GrpcResponseDomainModel.CallResult.Success ->
                GrpcItemViewState.StatusViewState.Success(text = "Success")
        }
    },
    requestTimeFormatted = formatTimestamp(domainModel.request.timestamp),
    durationFormatted = domainModel.response?.let {
        val duration = it.timestamp - domainModel.request.timestamp
        formatDuration(duration.toDouble())
    },
)

fun toDetailUi(domainModel: GrpcCallDomainModel): GrpcDetailViewState = GrpcDetailViewState(
    method = domainModel.request.method,
    url = domainModel.request.authority,
    status = if (domainModel.response == null) {
        GrpcItemViewState.StatusViewState.Waiting(text = "Waiting")
    } else {
        when (domainModel.response.result) {
            is GrpcResponseDomainModel.CallResult.Error ->
                GrpcItemViewState.StatusViewState.Failure(domainModel.response.result.cause)

            is GrpcResponseDomainModel.CallResult.Success ->
                GrpcItemViewState.StatusViewState.Success(text = "Success")
        }
    },
    requestTimeFormatted = formatTimestamp(domainModel.request.timestamp),
    durationFormatted = domainModel.response?.let {
        val duration = it.timestamp - domainModel.request.timestamp
        formatDuration(duration.toDouble())
    },
    requestBody = domainModel.request.data,
    requestHeaders = domainModel.request.headers.map {
        toUi(it)
    },
    response = domainModel.response?.let {
        GrpcDetailViewState.ResponseViewState(
            headers = it.headers.map {
                toUi(it)
            },
            result = when (it.result) {
                is GrpcResponseDomainModel.CallResult.Error -> GrpcDetailViewState.DetailPayload.Failure(it.result.cause)
                is GrpcResponseDomainModel.CallResult.Success -> GrpcDetailViewState.DetailPayload.Success(it.result.data)
            },
        )
    },
)

fun toUi(header: GrpcHeaderDomainModel) = NetworkDetailHeaderUi(
    name = header.key,
    value = header.value,
)

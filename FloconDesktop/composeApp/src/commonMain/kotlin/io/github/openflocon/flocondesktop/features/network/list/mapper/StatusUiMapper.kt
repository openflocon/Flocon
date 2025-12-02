package io.github.openflocon.flocondesktop.features.network.list.mapper

import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkStatusUi

fun loadingStatus() = NetworkStatusUi(
    text = "Loading",
    status = NetworkStatusUi.Status.LOADING,
)

fun failureStatus() = NetworkStatusUi(
    text = "Exception",
    status = NetworkStatusUi.Status.EXCEPTION,
)

fun getStatusUi(networkCall: FloconNetworkCallDomainModel): NetworkStatusUi = networkCall.response?.let { response ->
    when (response) {
        is FloconNetworkCallDomainModel.Response.Failure -> failureStatus()
        is FloconNetworkCallDomainModel.Response.Success -> when (val s = response.specificInfos) {
            is FloconNetworkCallDomainModel.Response.Success.SpecificInfos.GraphQl -> toGraphQlNetworkStatusUi(isSuccess = s.isSuccess)
            is FloconNetworkCallDomainModel.Response.Success.SpecificInfos.Http -> toNetworkStatusUi(s.httpCode)
            is FloconNetworkCallDomainModel.Response.Success.SpecificInfos.Grpc -> toGrpcNetworkStatusUi(networkCall)
        }
    }
} ?: when (val s = networkCall.request.specificInfos) {
    is FloconNetworkCallDomainModel.Request.SpecificInfos.GraphQl,
    is FloconNetworkCallDomainModel.Request.SpecificInfos.Grpc,
    is FloconNetworkCallDomainModel.Request.SpecificInfos.Http -> loadingStatus()
    is FloconNetworkCallDomainModel.Request.SpecificInfos.WebSocket -> {
        NetworkStatusUi(
            text = s.event,
            status = if (s.event == "error") NetworkStatusUi.Status.ERROR else NetworkStatusUi.Status.SUCCESS,
        )
    }
}

fun toNetworkStatusUi(code: Int): NetworkStatusUi = NetworkStatusUi(
    text = code.toString(),
    status = if (code >= 200 && code < 400) NetworkStatusUi.Status.SUCCESS else NetworkStatusUi.Status.ERROR,
)

fun toGraphQlNetworkStatusUi(isSuccess: Boolean): NetworkStatusUi = NetworkStatusUi(
    text = if (isSuccess) "Success" else "Error",
    status = if (isSuccess) NetworkStatusUi.Status.SUCCESS else NetworkStatusUi.Status.ERROR,
)

fun toGrpcNetworkStatusUi(call: FloconNetworkCallDomainModel): NetworkStatusUi {
    val response = call.response ?: return loadingStatus()
    return when (response) {
        is FloconNetworkCallDomainModel.Response.Failure -> failureStatus()
        is FloconNetworkCallDomainModel.Response.Success -> {
            when (val s = response.specificInfos) {
                is FloconNetworkCallDomainModel.Response.Success.SpecificInfos.Grpc -> {
                    val isSuccess = s.grpcStatus == "OK"
                    NetworkStatusUi(
                        text = s.grpcStatus,
                        status = if (isSuccess) NetworkStatusUi.Status.SUCCESS else NetworkStatusUi.Status.ERROR,
                    )
                }
                else -> NetworkStatusUi(
                    text = "not a grpc response",
                    status = NetworkStatusUi.Status.ERROR,
                )
            }
        }
    }
}

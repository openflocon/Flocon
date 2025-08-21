package io.github.openflocon.flocondesktop.features.network.list.mapper

import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkStatusUi

fun loadingStatus() = NetworkStatusUi(
    text = "Loading",
    status = NetworkStatusUi.Status.LOADING,
)

fun getStatusUi(networkCall: FloconNetworkCallDomainModel): NetworkStatusUi = networkCall.response?.let { response ->
    when(response) {
        is FloconNetworkCallDomainModel.Response.Failure -> NetworkStatusUi(
            text = response.issue,
            status = NetworkStatusUi.Status.ERROR,
        )
        is FloconNetworkCallDomainModel.Response.Success -> when (val s = response.specificInfos) {
            is FloconNetworkCallDomainModel.Response.Success.SpecificInfos.GraphQl -> toGraphQlNetworkStatusUi(isSuccess = s.isSuccess)
            is FloconNetworkCallDomainModel.Response.Success.SpecificInfos.Http -> toNetworkStatusUi(s.httpCode)
            is FloconNetworkCallDomainModel.Response.Success.SpecificInfos.Grpc -> toGrpcNetworkStatusUi(networkCall)
        }
    }
} ?: loadingStatus()

fun toNetworkStatusUi(code: Int): NetworkStatusUi = NetworkStatusUi(
    text = code.toString(),
    status = if (code >= 200 && code < 300) NetworkStatusUi.Status.SUCCESS else NetworkStatusUi.Status.ERROR,
)

fun toGraphQlNetworkStatusUi(isSuccess: Boolean): NetworkStatusUi = NetworkStatusUi(
    text = if (isSuccess) "Success" else "Error",
    status = if (isSuccess) NetworkStatusUi.Status.SUCCESS else NetworkStatusUi.Status.ERROR,
)

fun toGrpcNetworkStatusUi(call: FloconNetworkCallDomainModel): NetworkStatusUi {
    val response = call.response ?: return loadingStatus()
    return when(response) {
        is FloconNetworkCallDomainModel.Response.Failure -> NetworkStatusUi(
            text = response.issue,
            status = NetworkStatusUi.Status.ERROR,
        )
        is FloconNetworkCallDomainModel.Response.Success -> {
            when(val s = response.specificInfos) {
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

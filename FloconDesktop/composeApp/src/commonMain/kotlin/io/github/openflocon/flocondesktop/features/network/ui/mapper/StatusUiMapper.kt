package io.github.openflocon.flocondesktop.features.network.ui.mapper

import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkStatusUi


fun loadingStatus() = NetworkStatusUi(
    text = "Loading",
    status = NetworkStatusUi.Status.LOADING,
)

fun getStatusUi(networkCall: FloconNetworkCallDomainModel): NetworkStatusUi = networkCall.networkResponse?.let { response ->
    when(val t = networkCall) {
        is FloconNetworkCallDomainModel.GraphQl -> toGraphQlNetworkStatusUi(isSuccess = t.response!!.isSuccess)
        is FloconNetworkCallDomainModel.Http -> toNetworkStatusUi(networkCall.response!!.httpCode)
        is FloconNetworkCallDomainModel.Grpc -> toGrpcNetworkStatusUi(t)
    }
} ?: loadingStatus()

fun toNetworkStatusUi(code: Int): NetworkStatusUi = NetworkStatusUi(
    text = code.toString(),
    status = if(code >= 200 && code < 300) NetworkStatusUi.Status.SUCCESS else NetworkStatusUi.Status.ERROR
)

fun toGraphQlNetworkStatusUi(isSuccess: Boolean): NetworkStatusUi = NetworkStatusUi(
    text = if (isSuccess) "Success" else "Error",
    status = if(isSuccess) NetworkStatusUi.Status.SUCCESS else NetworkStatusUi.Status.ERROR,
)

fun toGrpcNetworkStatusUi(call: FloconNetworkCallDomainModel.Grpc): NetworkStatusUi {
    val response = call.response ?: return loadingStatus()
    val isSuccess = response.responseStatus == "OK"
    return NetworkStatusUi(
        text = response.responseStatus,
        status = if(isSuccess) NetworkStatusUi.Status.SUCCESS else NetworkStatusUi.Status.ERROR,
    )
}

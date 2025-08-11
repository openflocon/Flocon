package io.github.openflocon.flocondesktop.features.network.ui.mapper

import io.github.openflocon.domain.models.FloconHttpRequestDomainModel
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkStatusUi

fun getStatusUi(httpRequest: FloconHttpRequestDomainModel): NetworkStatusUi = when (val t = httpRequest.type) {
    is FloconHttpRequestDomainModel.Type.GraphQl -> toGraphQlNetworkStatusUi(isSuccess = t.isSuccess)
    is FloconHttpRequestDomainModel.Type.Http -> toNetworkStatusUi(t.httpCode)
    is FloconHttpRequestDomainModel.Type.Grpc -> toGrpcNetworkStatusUi(t)
}

fun toNetworkStatusUi(code: Int): NetworkStatusUi = NetworkStatusUi(
    text = code.toString(),
    isSuccess = code >= 200 && code < 300,
)

fun toGraphQlNetworkStatusUi(isSuccess: Boolean): NetworkStatusUi = NetworkStatusUi(
    text = if (isSuccess) "Success" else "Error",
    isSuccess = isSuccess,
)

fun toGrpcNetworkStatusUi(type: FloconHttpRequestDomainModel.Type.Grpc): NetworkStatusUi {
    val isSuccess = type.responseStatus == "OK"
    return NetworkStatusUi(
        text = type.responseStatus,
        isSuccess = isSuccess,
    )
}

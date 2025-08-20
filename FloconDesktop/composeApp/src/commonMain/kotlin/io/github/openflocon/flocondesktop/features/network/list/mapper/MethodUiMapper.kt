package io.github.openflocon.flocondesktop.features.network.list.mapper

import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkMethodUi

fun getMethodUi(networkRequest: FloconNetworkCallDomainModel): NetworkMethodUi = when (networkRequest) {
    is FloconNetworkCallDomainModel.GraphQl -> when (val t = networkRequest.request.operationType.lowercase()) {
        "query" -> NetworkMethodUi.GraphQl.QUERY
        "mutation" -> NetworkMethodUi.GraphQl.MUTATION
        else -> NetworkMethodUi.OTHER(networkRequest.request.operationType, icon = null)
    }
    is FloconNetworkCallDomainModel.Http -> toHttpMethodUi(networkRequest.networkRequest.method)
    is FloconNetworkCallDomainModel.Grpc -> NetworkMethodUi.Grpc
}

fun toHttpMethodUi(httpMethod: String): NetworkMethodUi = when (httpMethod.lowercase()) {
    "get" -> NetworkMethodUi.Http.GET
    "put" -> NetworkMethodUi.Http.PUT
    "post" -> NetworkMethodUi.Http.POST
    "delete" -> NetworkMethodUi.Http.DELETE
    else -> NetworkMethodUi.OTHER(httpMethod, icon = null)
}

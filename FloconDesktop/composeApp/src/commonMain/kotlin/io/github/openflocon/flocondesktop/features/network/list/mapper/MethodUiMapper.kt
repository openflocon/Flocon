package io.github.openflocon.flocondesktop.features.network.list.mapper

import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkMethodUi.*

fun getMethodUi(networkCall: FloconNetworkCallDomainModel): NetworkMethodUi = when (val s = networkCall.request.specificInfos) {
    is FloconNetworkCallDomainModel.Request.SpecificInfos.GraphQl -> when (val t = s.operationType.lowercase()) {
        "query" -> NetworkMethodUi.GraphQl.QUERY
        "mutation" -> NetworkMethodUi.GraphQl.MUTATION
        else -> OTHER(s.operationType, icon = null)
    }
    is FloconNetworkCallDomainModel.Request.SpecificInfos.Http -> toHttpMethodUi(networkCall.request.method)
    is FloconNetworkCallDomainModel.Request.SpecificInfos.Grpc -> NetworkMethodUi.Grpc
    is FloconNetworkCallDomainModel.Request.SpecificInfos.WebSocket -> NetworkMethodUi.WebSocket
}

fun toHttpMethodUi(httpMethod: String): NetworkMethodUi = when (httpMethod.lowercase()) {
    "get" -> NetworkMethodUi.Http.GET
    "put" -> NetworkMethodUi.Http.PUT
    "post" -> NetworkMethodUi.Http.POST
    "delete" -> NetworkMethodUi.Http.DELETE
    else -> NetworkMethodUi.OTHER(httpMethod, icon = null)
}

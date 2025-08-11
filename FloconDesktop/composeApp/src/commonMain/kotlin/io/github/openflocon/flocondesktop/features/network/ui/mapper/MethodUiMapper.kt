package io.github.openflocon.flocondesktop.features.network.ui.mapper

import com.flocon.library.domain.models.FloconHttpRequestDomainModel
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkMethodUi.OTHER

fun getMethodUi(httpRequest: FloconHttpRequestDomainModel): NetworkMethodUi = when (val t = httpRequest.type) {
    is FloconHttpRequestDomainModel.Type.GraphQl -> when (t.operationType.lowercase()) {
        "query" -> NetworkMethodUi.GraphQl.QUERY
        "mutation" -> NetworkMethodUi.GraphQl.MUTATION
        else -> OTHER(t.operationType, icon = null)
    }
    is FloconHttpRequestDomainModel.Type.Http -> toHttpMethodUi(httpRequest.request.method)
    is FloconHttpRequestDomainModel.Type.Grpc -> NetworkMethodUi.Grpc
}

fun toHttpMethodUi(httpMethod: String): NetworkMethodUi = when (httpMethod.lowercase()) {
    "get" -> NetworkMethodUi.Http.GET
    "put" -> NetworkMethodUi.Http.PUT
    "post" -> NetworkMethodUi.Http.POST
    "delete" -> NetworkMethodUi.Http.DELETE
    else -> NetworkMethodUi.OTHER(httpMethod, icon = null)
}

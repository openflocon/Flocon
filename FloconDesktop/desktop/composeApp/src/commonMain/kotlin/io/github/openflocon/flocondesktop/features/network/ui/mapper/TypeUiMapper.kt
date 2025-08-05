package io.github.openflocon.flocondesktop.features.network.ui.mapper

import io.github.openflocon.flocondesktop.features.network.domain.model.FloconHttpRequestDomainModel
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkItemViewState

fun toTypeUi(networkRequest: FloconHttpRequestDomainModel): NetworkItemViewState.NetworkTypeUi = when (val t = networkRequest.type) {
    is FloconHttpRequestDomainModel.Type.GraphQl -> NetworkItemViewState.NetworkTypeUi.GraphQl(
        queryName = t.query,
    )

    is FloconHttpRequestDomainModel.Type.Http -> {
        val query = extractPath(networkRequest.url)
        NetworkItemViewState.NetworkTypeUi.Url(
            query = query,
            method = networkRequest.request.method
        )
    }

    is FloconHttpRequestDomainModel.Type.Grpc -> {
        NetworkItemViewState.NetworkTypeUi.Grpc(
            method = networkRequest.request.method,
        )
    }
}

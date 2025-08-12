package io.github.openflocon.flocondesktop.features.network.ui.mapper

import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkItemViewState

fun toTypeUi(call: FloconNetworkCallDomainModel): NetworkItemViewState.NetworkTypeUi = when (call) {
    is FloconNetworkCallDomainModel.GraphQl -> NetworkItemViewState.NetworkTypeUi.GraphQl(
        queryName = call.request.query,
    )

    is FloconNetworkCallDomainModel.Http -> {
        val query = extractPath(call.networkRequest.url)
        NetworkItemViewState.NetworkTypeUi.Url(
            query = query,
            method = call.networkRequest.method,
        )
    }

    is FloconNetworkCallDomainModel.Grpc -> {
        NetworkItemViewState.NetworkTypeUi.Grpc(
            method = call.networkRequest.method,
        )
    }
}

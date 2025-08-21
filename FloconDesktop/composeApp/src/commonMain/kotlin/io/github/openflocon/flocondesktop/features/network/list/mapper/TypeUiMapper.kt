package io.github.openflocon.flocondesktop.features.network.list.mapper

import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkItemViewState

fun toTypeUi(call: FloconNetworkCallDomainModel): NetworkItemViewState.NetworkTypeUi = when (val s = call.request.specificInfos) {
    is FloconNetworkCallDomainModel.Request.SpecificInfos.GraphQl -> NetworkItemViewState.NetworkTypeUi.GraphQl(
        queryName = s.query,
    )

    is FloconNetworkCallDomainModel.Request.SpecificInfos.Http -> {
        val query = extractPath(call.request.url)
        NetworkItemViewState.NetworkTypeUi.Url(
            query = query,
            method = call.request.method,
        )
    }

    is FloconNetworkCallDomainModel.Request.SpecificInfos.Grpc -> {
        NetworkItemViewState.NetworkTypeUi.Grpc(
            method = call.request.method,
        )
    }
}

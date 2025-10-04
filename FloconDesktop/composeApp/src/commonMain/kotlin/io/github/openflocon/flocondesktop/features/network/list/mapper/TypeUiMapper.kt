package io.github.openflocon.flocondesktop.features.network.list.mapper

import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkItemViewState.NetworkTypeUi
import io.ktor.client.plugins.websocket.WebSockets

fun toTypeUi(call: FloconNetworkCallDomainModel): NetworkItemViewState.NetworkTypeUi = when (val s = call.request.specificInfos) {
    is FloconNetworkCallDomainModel.Request.SpecificInfos.GraphQl -> NetworkTypeUi.GraphQl(
        queryName = call.request.queryFormatted,
    )

    is FloconNetworkCallDomainModel.Request.SpecificInfos.Http -> {
        NetworkTypeUi.Url(
            query = call.request.queryFormatted,
        )
    }

    is FloconNetworkCallDomainModel.Request.SpecificInfos.Grpc -> {
        NetworkTypeUi.Grpc(
            method = call.request.queryFormatted,
        )
    }

    FloconNetworkCallDomainModel.Request.SpecificInfos.WebSocket -> NetworkTypeUi.WebSocket(
        text = call.request.queryFormatted,
    )
}

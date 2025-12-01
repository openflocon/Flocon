package io.github.openflocon.flocondesktop.features.network.detail

import io.github.openflocon.flocondesktop.features.network.detail.model.NetworkDetailViewState

sealed interface NetworkDetailAction {

    data class DisplayBearerJwt(val token: String) : NetworkDetailAction

    data class JsonDetail(val json: String) : NetworkDetailAction

    data class CopyText(val text: String) : NetworkDetailAction

    sealed interface OpenBodyExternally : NetworkDetailAction {
        data class Request(val item: NetworkDetailViewState) : OpenBodyExternally
        data class Response(val item: NetworkDetailViewState.Response.Success) : OpenBodyExternally
    }

}

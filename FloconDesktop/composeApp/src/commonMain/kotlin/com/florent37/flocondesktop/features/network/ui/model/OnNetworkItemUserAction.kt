package com.florent37.flocondesktop.features.network.ui.model

sealed interface OnNetworkItemUserAction {
    data class OnClicked(
        val item: NetworkItemViewState,
    ) : OnNetworkItemUserAction

    data class CopyUrl(
        val item: NetworkItemViewState,
    ) : OnNetworkItemUserAction

    data class CopyCUrl(
        val item: NetworkItemViewState,
    ) : OnNetworkItemUserAction

    data class Remove(
        val item: NetworkItemViewState,
    ) : OnNetworkItemUserAction

    data class RemoveLinesAbove(
        val item: NetworkItemViewState,
    ) : OnNetworkItemUserAction
}

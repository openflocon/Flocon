package io.github.openflocon.flocondesktop.features.network.ui

import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkItemViewState

sealed interface NetworkAction {

    data class SelectRequest(val id: String) : NetworkAction

    data class CopyText(val text: String) : NetworkAction

    data object ClosePanel : NetworkAction

    data object Reset : NetworkAction

    data class CopyUrl(
        val item: NetworkItemViewState,
    ) : NetworkAction

    data class CopyCUrl(
        val item: NetworkItemViewState,
    ) : NetworkAction

    data class Remove(
        val item: NetworkItemViewState,
    ) : NetworkAction

    data class RemoveLinesAbove(
        val item: NetworkItemViewState,
    ) : NetworkAction

}

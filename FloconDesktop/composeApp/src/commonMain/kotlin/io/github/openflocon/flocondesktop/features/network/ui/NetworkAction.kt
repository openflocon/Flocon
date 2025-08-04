package io.github.openflocon.flocondesktop.features.network.ui

import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.ui.view.filters.MethodFilter

sealed interface NetworkAction {

    data class SelectRequest(val id: String) : NetworkAction

    data class CopyText(val text: String) : NetworkAction

    data object ClosePanel : NetworkAction

    data object Reset : NetworkAction

    data class CopyUrl(val item: NetworkItemViewState) : NetworkAction

    data class CopyCUrl(val item: NetworkItemViewState) : NetworkAction

    data class Remove(val item: NetworkItemViewState) : NetworkAction

    data class RemoveLinesAbove(val item: NetworkItemViewState) : NetworkAction

    data class FilterQuery(val query: String) : NetworkAction

    data class FilterMethod(val method: NetworkMethodUi, val add: Boolean) : NetworkAction

}

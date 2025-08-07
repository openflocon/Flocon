package io.github.openflocon.flocondesktop.features.network.ui

import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.ui.model.SortedByUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.header.OnFilterAction
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.NetworkColumnsTypeUiModel

sealed interface NetworkAction {

    data class SelectRequest(val id: String) : NetworkAction

    data class CopyText(val text: String) : NetworkAction

    data object ClosePanel : NetworkAction

    data object Reset : NetworkAction

    data class JsonDetail(val id: String, val json: String) : NetworkAction

    data class CloseJsonDetail(val id: String) : NetworkAction

    data class CopyUrl(val item: NetworkItemViewState) : NetworkAction

    data class CopyCUrl(val item: NetworkItemViewState) : NetworkAction

    data class Remove(val item: NetworkItemViewState) : NetworkAction

    data class RemoveLinesAbove(val item: NetworkItemViewState) : NetworkAction

    data class FilterQuery(val query: String) : NetworkAction

    sealed interface HeaderAction : NetworkAction {
        data class ClickOnSort(
            val type: NetworkColumnsTypeUiModel,
            val sort: SortedByUiModel.Enabled
        ) : HeaderAction

        data class FilterAction(
            val action: OnFilterAction,
        ) : HeaderAction
    }
}

package io.github.openflocon.flocondesktop.features.network.list.model

import io.github.openflocon.flocondesktop.features.network.detail.NetworkDetailAction
import io.github.openflocon.flocondesktop.features.network.list.model.header.OnFilterAction
import io.github.openflocon.flocondesktop.features.network.list.model.header.columns.NetworkColumnsTypeUiModel

sealed interface NetworkAction {

    data class SelectRequest(val id: String) : NetworkAction

    data object ClosePanel : NetworkAction

    data object Reset : NetworkAction

    data class CreateMock(val item: NetworkItemViewState) : NetworkAction

    data object OpenMocks : NetworkAction

    data object OpenWebsocketMocks : NetworkAction

    data class UpdateDisplayOldSessions(val value: Boolean) : NetworkAction
    data object OpenBadNetworkQuality : NetworkAction
    data object CloseBadNetworkQuality : NetworkAction
    data object CloseWebsocketMocks : NetworkAction

    data object ExportCsv : NetworkAction
    data object ImportFromCsv : NetworkAction

    data class CopyUrl(val item: NetworkItemViewState) : NetworkAction

    data class CopyCUrl(val item: NetworkItemViewState) : NetworkAction

    data class Replay(val item: NetworkItemViewState) : NetworkAction

    data class Remove(val item: NetworkItemViewState) : NetworkAction

    data class RemoveLinesAbove(val item: NetworkItemViewState) : NetworkAction

    data class FilterQuery(val query: String) : NetworkAction

    data class InvertList(val value: Boolean) : NetworkAction

    data class Pinned(val value: Boolean) : NetworkAction

    data class ToggleAutoScroll(val value: Boolean) : NetworkAction

    data object ClearOldSession : NetworkAction

    data class DetailAction(val action: NetworkDetailAction) : NetworkAction

    data class Up(
        val itemIdToSelect: String,
    ) : NetworkAction

    data class Down(
        val itemIdToSelect: String,
    ) : NetworkAction

    sealed interface HeaderAction : NetworkAction {
        data class ClickOnSort(
            val type: NetworkColumnsTypeUiModel,
            val sort: SortedByUiModel.Enabled,
        ) : HeaderAction

        data class FilterAction(
            val action: OnFilterAction,
        ) : HeaderAction
    }
}

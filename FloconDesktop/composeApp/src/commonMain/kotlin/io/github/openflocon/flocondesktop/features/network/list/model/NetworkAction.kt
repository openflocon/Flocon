package io.github.openflocon.flocondesktop.features.network.list.model

import io.github.openflocon.flocondesktop.features.network.list.model.header.OnFilterAction
import io.github.openflocon.flocondesktop.features.network.list.model.header.columns.NetworkColumnsTypeUiModel

sealed interface NetworkAction {

    data class SelectRequest(val id: String) : NetworkAction

    data class CopyText(val text: String) : NetworkAction

    data object ClosePanel : NetworkAction

    data object Reset : NetworkAction

    data class JsonDetail(val id: String, val json: String) : NetworkAction

    data class DisplayBearerJwt(val token: String) : NetworkAction

    data class CreateMock(val item: NetworkItemViewState) : NetworkAction

    data object OpenMocks : NetworkAction
    data object CloseMocks : NetworkAction

    data object OpenBadNetworkQuality : NetworkAction
    data object CloseBadNetworkQuality : NetworkAction

    data object ExportCsv : NetworkAction

    data class CloseJsonDetail(val id: String) : NetworkAction

    data class CopyUrl(val item: NetworkItemViewState) : NetworkAction

    data class CopyCUrl(val item: NetworkItemViewState) : NetworkAction

    data class Remove(val item: NetworkItemViewState) : NetworkAction

    data class RemoveLinesAbove(val item: NetworkItemViewState) : NetworkAction

    data class FilterQuery(val query: String) : NetworkAction

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

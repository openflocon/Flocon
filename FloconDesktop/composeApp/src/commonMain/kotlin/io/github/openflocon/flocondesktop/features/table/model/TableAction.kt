package io.github.openflocon.flocondesktop.features.table.model

import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsRowUiModel

sealed interface TableAction {
    data class OnClick(
        val item: TableRowUiModel
    ) : TableAction

    data class Remove(
        val item: TableRowUiModel
    ) : TableAction

    data class RemoveLinesAbove(
        val item: TableRowUiModel
    ) : TableAction

    data object ClosePanel: TableAction
}

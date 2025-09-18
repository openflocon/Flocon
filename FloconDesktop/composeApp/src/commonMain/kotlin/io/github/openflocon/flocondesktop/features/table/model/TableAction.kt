package io.github.openflocon.flocondesktop.features.table.model

sealed interface TableAction {
    data class Remove(
        val item: TableRowUiModel
    ) : TableAction

    data class RemoveLinesAbove(
        val item: TableRowUiModel
    ) : TableAction

    data object ExportCsv : TableAction
}

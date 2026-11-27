package io.github.openflocon.flocondesktop.features.database.model

sealed interface DatabaseTabAction {
    data class UpdateAutoUpdate(val value: Boolean) : DatabaseTabAction
    data object ClearQuery : DatabaseTabAction
    data class ExecuteQuery(val query: String) : DatabaseTabAction
    data object Copy : DatabaseTabAction
    data object Import : DatabaseTabAction
    data class SaveFavorite(val title: String) : DatabaseTabAction
    data object ExportCsv : DatabaseTabAction

    /** Opens the cell in a text field, when its row can be updated. */
    data class StartCellEdit(val rowIndex: Int, val columnIndex: Int) : DatabaseTabAction
    data object CancelCellEdit : DatabaseTabAction

    /** Writes the value of the cell on the device, if it is still the one being edited. */
    data class CommitCellEdit(val rowIndex: Int, val columnIndex: Int, val value: String) : DatabaseTabAction
    data class SetCellNull(val rowIndex: Int, val columnIndex: Int) : DatabaseTabAction
    data class CopyCellValue(val value: String) : DatabaseTabAction

    /** The whole row as markdown. */
    data class CopyRow(val rowIndex: Int) : DatabaseTabAction

    /** Opens the detail panel of the row. */
    data class ShowRowDetail(val rowIndex: Int) : DatabaseTabAction
}

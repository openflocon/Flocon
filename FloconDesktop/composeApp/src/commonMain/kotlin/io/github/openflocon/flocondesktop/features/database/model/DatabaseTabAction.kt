package io.github.openflocon.flocondesktop.features.database.model

sealed interface DatabaseTabAction {
    data class UpdateAutoUpdate(val value: Boolean) : DatabaseTabAction
    data object ClearQuery : DatabaseTabAction
    data class ExecuteQuery(val query: String) : DatabaseTabAction
    data object Copy : DatabaseTabAction
    data object Import : DatabaseTabAction
    data class SaveFavorite(val title: String) : DatabaseTabAction
    data object ExportCsv : DatabaseTabAction
}

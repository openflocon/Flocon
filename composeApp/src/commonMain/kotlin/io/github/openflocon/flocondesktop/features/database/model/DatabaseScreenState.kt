package io.github.openflocon.flocondesktop.features.database.model

sealed interface DatabaseScreenState {
    data object Idle : DatabaseScreenState
    data class Result(
        val result: QueryResultUiModel,
        val cellEdit: DatabaseCellEditUiModel? = null,
    ) : DatabaseScreenState
}

fun previewDatabaseScreenState() = DatabaseScreenState.Idle

package io.github.openflocon.flocondesktop.features.database.model

sealed interface DatabaseScreenState {
    data object Idle : DatabaseScreenState
    data class Result(val result: QueryResultUiModel) : DatabaseScreenState
}

fun previewDatabaseScreenState() = DatabaseScreenState.Idle

package com.florent37.flocondesktop.features.database.ui.model

sealed interface DatabaseScreenState {
    data object Idle : DatabaseScreenState
    data class Result(val result: QueryResultUiModel) : DatabaseScreenState
    data class Queries(val queries: List<String>) : DatabaseScreenState
}

fun previewDatabaseScreenStateQueries() = DatabaseScreenState.Queries(
    queries = listOf(
        "SELECT * FROM TOTO",
        "SELECT * FROM DOGS WHERE owner = \"florent\"",
        "SELECT * FROM DOGS, HOUSE WHERE owner = \"florent\" AND DOGS.house_id = HOUSE.id",
    ),
)

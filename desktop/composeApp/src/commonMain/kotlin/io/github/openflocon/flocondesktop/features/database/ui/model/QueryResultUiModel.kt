package io.github.openflocon.flocondesktop.features.database.ui.model

sealed interface QueryResultUiModel {
    data class Text(
        val text: String,
    ) : QueryResultUiModel

    data class Values(
        val columns: List<String>,
        val rows: List<DatabaseRowUiModel>,
    ) : QueryResultUiModel
}

data class DatabaseRowUiModel(
    val items: List<String?>,
)

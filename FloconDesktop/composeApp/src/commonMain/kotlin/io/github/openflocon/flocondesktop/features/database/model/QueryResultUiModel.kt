package io.github.openflocon.flocondesktop.features.database.model

import kotlinx.serialization.Serializable

sealed interface QueryResultUiModel {
    data class Text(
        val text: String,
    ) : QueryResultUiModel

    data class Values(
        val columns: List<String>,
        val rows: List<DatabaseRowUiModel>,
    ) : QueryResultUiModel
}

@Serializable
data class DatabaseRowUiModel(
    val items: List<String?>,
)

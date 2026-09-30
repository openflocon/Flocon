package io.github.openflocon.flocondesktop.features.database.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.serialization.Serializable

sealed interface QueryResultUiModel {
    data class Text(
        val text: String,
    ) : QueryResultUiModel

    data class Values(
        val columns: List<String>,
        val rows: ImmutableList<DatabaseRowUiModel>,
    ) : QueryResultUiModel
}

@Serializable
data class DatabaseRowUiModel(
    val items: List<String?>,
)

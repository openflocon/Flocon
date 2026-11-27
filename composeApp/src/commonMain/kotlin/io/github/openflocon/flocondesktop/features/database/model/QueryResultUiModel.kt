package io.github.openflocon.flocondesktop.features.database.model

import kotlinx.collections.immutable.ImmutableList

sealed interface QueryResultUiModel {
    data class Text(
        val text: String,
    ) : QueryResultUiModel

    data class Values(
        val columns: List<String>,
        val rows: ImmutableList<DatabaseRowUiModel>,
        val editTarget: DatabaseRowEditTargetUiModel? = null,
    ) : QueryResultUiModel
}

data class DatabaseRowUiModel(
    val items: List<String?>,
)

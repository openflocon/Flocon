package io.github.openflocon.flocondesktop.features.database.model

import androidx.compose.runtime.Immutable

/** Where the rows of a result come from, when they can be updated. */
@Immutable
data class DatabaseRowEditTargetUiModel(
    val deviceId: String,
    val packageName: String,
    val databaseId: String,
    val tableName: String,
    val columns: List<Column>,
) {
    data class Column(
        val name: String,
        val isPrimaryKey: Boolean,
        val affinity: Affinity,
    )

    enum class Affinity { Integer, Text, Blob, Real, Numeric }
}

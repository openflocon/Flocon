package io.github.openflocon.flocondesktop.features.database.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

/** Where the rows of a result come from, when they can be updated. */
@Immutable
@Serializable
data class DatabaseRowEditTargetUiModel(
    val deviceId: String,
    val packageName: String,
    val databaseId: String,
    val tableName: String,
    val columns: List<Column>,
) {
    @Serializable
    data class Column(
        val name: String,
        val isPrimaryKey: Boolean,
        val affinity: Affinity,
        val isNullable: Boolean,
    )

    enum class Affinity { Integer, Text, Blob, Real, Numeric }
}

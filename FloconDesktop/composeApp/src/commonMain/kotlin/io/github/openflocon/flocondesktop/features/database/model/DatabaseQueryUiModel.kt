package io.github.openflocon.flocondesktop.features.database.model

import androidx.compose.runtime.Immutable

@Immutable
data class DatabaseQueryUiModel(
    val sqlQuery: String,
    val bindArgs: List<String>?,
    val dateFormatted: String,
    val isTransaction: Boolean,
    val isFromOldSession: Boolean,
    val type: Type?,
) {
    enum class Type {
        Select,
        Insert,
        Update,
        Delete,
        Transaction,
    }
}

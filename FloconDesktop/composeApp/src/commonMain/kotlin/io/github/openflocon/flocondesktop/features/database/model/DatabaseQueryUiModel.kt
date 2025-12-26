package io.github.openflocon.flocondesktop.features.database.model

import androidx.compose.runtime.Immutable

@Immutable
data class DatabaseQueryUiModel(
    val sqlQuery: String,
    val bindArgs: List<String>?,
    val timestamp: Long,
)

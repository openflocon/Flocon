package io.github.openflocon.flocondesktop.features.database.model

import androidx.compose.runtime.Immutable

@Immutable
data class DatabaseFavoriteQueryUiModel(
    val id: Long,
    val databaseId: String,
    val title: String,
)
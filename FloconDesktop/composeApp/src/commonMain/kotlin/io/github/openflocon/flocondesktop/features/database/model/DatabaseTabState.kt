package io.github.openflocon.flocondesktop.features.database.model

import androidx.compose.runtime.Immutable

@Immutable
data class DatabaseTabState(
    val databaseId: String,
    val tableName: String?,
    val generatedName: String,
    val index: Int,
) {
    val displayName: String = generatedName + if(index > 0) " (${index})" else ""
}

package io.github.openflocon.flocondesktop.features.table.model

import androidx.compose.runtime.Immutable

@Immutable
data class TableRowUiModel(
    val id: String,
    val values: List<String>,
) {
    fun contains(text: String): Boolean = values.any { it.contains(text, ignoreCase = true) }
}

fun previewTableRowUiModel() = TableRowUiModel(
    id = "",
    values = listOf("value1", "value2", "value3"),
)

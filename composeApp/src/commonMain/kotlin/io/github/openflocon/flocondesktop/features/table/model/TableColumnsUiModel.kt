package io.github.openflocon.flocondesktop.features.table.model

import androidx.compose.runtime.Immutable

@Immutable
data class TableColumnsUiModel(
    val columns: List<String>,
) {
    fun contains(text: String): Boolean = columns.any { it.contains(text, ignoreCase = true) }
}

fun previewTableColumnsUiModel() = TableColumnsUiModel(
    columns = listOf("column1", "column2", "column3"),
)

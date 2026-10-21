package io.github.openflocon.flocondesktop.features.table.model

import androidx.compose.runtime.Immutable

@Immutable
data class TableDetailUiModel(
    val tableName: String,
    val title: String,
    val dateFormatted: String,
    val columns: List<ColumnUiModel>,
) {
    @Immutable
    data class ColumnUiModel(
        val name: String,
        val value: String,
    )
}

fun previewTableDetailUiModel() = TableDetailUiModel(
    tableName = "table",
    title = "value0",
    dateFormatted = "2023-01-01 00:00:00",
    columns = List(5) {
        TableDetailUiModel.ColumnUiModel("column$it", "value$it")
    },
)

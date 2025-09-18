package io.github.openflocon.flocondesktop.features.table.model

import androidx.compose.runtime.Immutable

@Immutable
sealed interface TableContentStateUiModel {
    @Immutable
    data object Loading : TableContentStateUiModel

    @Immutable
    data object Empty : TableContentStateUiModel

    @Immutable
    data class WithContent(
        val columns: TableColumnsUiModel,
        val rows: List<TableRowUiModel>,
    ) : TableContentStateUiModel
}

fun TableContentStateUiModel.items(): List<TableRowUiModel> = when (this) {
    is TableContentStateUiModel.Empty,
    is TableContentStateUiModel.Loading,
    -> emptyList()
    is TableContentStateUiModel.WithContent -> rows
}

fun previewTableContentStateUiModel(): TableContentStateUiModel = TableContentStateUiModel.WithContent(
    columns = previewTableColumnsUiModel(),
    rows = listOf(
        previewTableRowUiModel(),
        previewTableRowUiModel(),
        previewTableRowUiModel(),
    ),
)

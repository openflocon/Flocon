package io.github.openflocon.flocondesktop.features.table.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
sealed interface TableContentStateUiModel {
    @Immutable
    data object Loading : TableContentStateUiModel

    @Immutable
    data object Empty : TableContentStateUiModel

    @Immutable
    data class WithContent(
        val columns: TableColumnsUiModel,
        val rows: ImmutableList<TableRowUiModel>,
    ) : TableContentStateUiModel
}

fun TableContentStateUiModel.items(): ImmutableList<TableRowUiModel> = when (this) {
    is TableContentStateUiModel.Empty,
    is TableContentStateUiModel.Loading,
    -> persistentListOf()
    is TableContentStateUiModel.WithContent -> rows
}

fun previewTableContentStateUiModel(): TableContentStateUiModel = TableContentStateUiModel.WithContent(
    columns = previewTableColumnsUiModel(),
    rows = persistentListOf(
        previewTableRowUiModel(),
        previewTableRowUiModel(),
        previewTableRowUiModel(),
    ),
)

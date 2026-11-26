package io.github.openflocon.flocondesktop.features.database.mapper

import io.github.openflocon.flocondesktop.features.database.model.DatabaseRowDetailUiModel
import io.github.openflocon.flocondesktop.features.database.model.DatabaseRowEditTargetUiModel
import kotlinx.collections.immutable.toImmutableList

fun toRowDetailUiModel(
    columns: List<String>,
    original: List<String?>,
    edited: List<String?>,
    editTarget: DatabaseRowEditTargetUiModel?,
    isSaving: Boolean,
): DatabaseRowDetailUiModel {
    val isEditable = editTarget.canEditRow(original)

    return DatabaseRowDetailUiModel(
        fields = columns.mapIndexed { index, column ->
            DatabaseRowDetailUiModel.Field(
                column = column,
                value = edited.getOrNull(index),
                isEditable = isEditable && editTarget?.canEditColumn(index) == true,
            )
        }.toImmutableList(),
        isEditable = isEditable,
        canUpdate = isEditable && !isSaving && original != edited,
    )
}

/** A row without its whole primary key can't be found back. */
fun DatabaseRowEditTargetUiModel?.canEditRow(values: List<String?>): Boolean = this != null &&
    columns.size == values.size &&
    columns.withIndex().none { (index, column) -> column.isPrimaryKey && values[index] == null }

/** Blobs are displayed as a placeholder, they can't be written back. */
fun DatabaseRowEditTargetUiModel.canEditColumn(index: Int): Boolean =
    columns.getOrNull(index)?.affinity.let { it != null && it != DatabaseRowEditTargetUiModel.Affinity.Blob }

/** The whole row as markdown. */
fun toRowMarkdown(columns: List<String>, values: List<String?>): String = buildString {
    columns.forEachIndexed { index, column ->
        append("### ")
        append(column)
        append(": ")
        append("\n")
        append("\n")
        appendLine(values.getOrNull(index) ?: "NULL")

        if (index != columns.lastIndex) {
            append("\n")
            appendLine("--------")
            append("\n")
        }
    }
}

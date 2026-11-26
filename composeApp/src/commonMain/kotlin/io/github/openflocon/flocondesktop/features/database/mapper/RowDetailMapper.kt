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
    // a row without its whole primary key can't be found back
    val isEditable = editTarget != null &&
        editTarget.columns.size == original.size &&
        editTarget.columns.withIndex().none { (index, column) -> column.isPrimaryKey && original[index] == null }

    return DatabaseRowDetailUiModel(
        fields = columns.mapIndexed { index, column ->
            DatabaseRowDetailUiModel.Field(
                column = column,
                value = edited.getOrNull(index),
                isEditable = isEditable && editTarget?.columns?.getOrNull(index)?.affinity.let { it != null && it != DatabaseRowEditTargetUiModel.Affinity.Blob },
            )
        }.toImmutableList(),
        isEditable = isEditable,
        canUpdate = isEditable && !isSaving && original != edited,
    )
}

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

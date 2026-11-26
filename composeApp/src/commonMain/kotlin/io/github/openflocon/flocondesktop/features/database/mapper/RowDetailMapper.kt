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
            val isFieldEditable = isEditable && editTarget?.canEditColumn(index) == true
            DatabaseRowDetailUiModel.Field(
                column = column,
                value = edited.getOrNull(index),
                isEditable = isFieldEditable,
                canSetNull = isFieldEditable && editTarget?.canSetNull(index) == true,
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

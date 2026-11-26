package io.github.openflocon.flocondesktop.features.database.mapper

import io.github.openflocon.flocondesktop.features.database.model.DatabaseRowEditTargetUiModel

/** A row without its whole primary key can't be found back. */
fun DatabaseRowEditTargetUiModel?.canEditRow(values: List<String?>): Boolean = this != null &&
    columns.size == values.size &&
    columns.withIndex().none { (index, column) -> column.isPrimaryKey && values[index] == null }

/** NOT NULL columns, and the primary key which finds the row back, can't be set to `NULL`. */
fun DatabaseRowEditTargetUiModel.canSetNull(index: Int): Boolean = columns.getOrNull(index)?.let { it.isNullable && !it.isPrimaryKey } == true

/** Blobs are displayed as a placeholder, they can't be written back. */
fun DatabaseRowEditTargetUiModel.canEditColumn(index: Int): Boolean = columns.getOrNull(index)?.affinity.let { it != null && it != DatabaseRowEditTargetUiModel.Affinity.Blob }

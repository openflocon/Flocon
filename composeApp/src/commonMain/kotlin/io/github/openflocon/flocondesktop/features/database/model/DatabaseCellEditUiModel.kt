package io.github.openflocon.flocondesktop.features.database.model

import androidx.compose.runtime.Immutable

/** The cell of the results being edited in place. */
@Immutable
data class DatabaseCellEditUiModel(
    val rowIndex: Int,
    val columnIndex: Int,
    /** The device refused the value: the field stays open to correct it. */
    val isInvalid: Boolean = false,
    val isSaving: Boolean = false,
) {
    fun isAt(rowIndex: Int, columnIndex: Int) = this.rowIndex == rowIndex && this.columnIndex == columnIndex
}

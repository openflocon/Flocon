package io.github.openflocon.flocondesktop.features.database.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class DatabaseRowDetailUiModel(
    val fields: ImmutableList<Field>,
    /** The row can be updated, the update button is displayed. */
    val isEditable: Boolean,
    /** Some values differ from the ones on the device. */
    val canUpdate: Boolean,
) {
    @Immutable
    data class Field(
        val column: String,
        val value: String?,
        val isEditable: Boolean,
        /** `false` for a NOT NULL column and for the primary key. */
        val canSetNull: Boolean,
    )
}

fun previewDatabaseRowDetailUiModel() = DatabaseRowDetailUiModel(
    fields = persistentListOf(
        DatabaseRowDetailUiModel.Field(column = "id", value = "1", isEditable = true, canSetNull = false),
        DatabaseRowDetailUiModel.Field(column = "name", value = "John Doe", isEditable = true, canSetNull = false),
        DatabaseRowDetailUiModel.Field(column = "city", value = null, isEditable = true, canSetNull = true),
        DatabaseRowDetailUiModel.Field(column = "avatar", value = "[blob]", isEditable = false, canSetNull = false),
    ),
    isEditable = true,
    canUpdate = true,
)

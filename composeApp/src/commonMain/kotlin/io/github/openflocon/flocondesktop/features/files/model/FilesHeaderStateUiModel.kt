package io.github.openflocon.flocondesktop.features.files.model

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.features.network.list.model.SortedByUiModel

@Immutable
data class FilesHeaderStateUiModel(
    val sortedBy: SortedBy?,
    val totalSizeFormatted: String?,
) {

    @Immutable
    data class SortedBy(
        val column: FileColumnUiModel,
        val sortedBy: SortedByUiModel.Enabled
    )
}

fun previewFilesHeaderStateUiModel() = FilesHeaderStateUiModel(
    sortedBy = null,
    totalSizeFormatted = "2 MB"
)

fun FilesHeaderStateUiModel.isSorted(columnUiModel: FileColumnUiModel): SortedByUiModel = this.sortedBy?.takeIf { it.column == columnUiModel }?.sortedBy ?: SortedByUiModel.None

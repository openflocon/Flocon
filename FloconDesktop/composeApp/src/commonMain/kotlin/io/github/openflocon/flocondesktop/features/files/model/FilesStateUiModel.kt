package io.github.openflocon.flocondesktop.features.files.model

import androidx.compose.runtime.Immutable

@Immutable
data class FilesStateUiModel(
    val backStack: List<FileUiModel>,
    val current: FileUiModel?,
    val numberOfFiles: Int?,
    val files: List<FileUiModel>,
    val headerState: FilesHeaderStateUiModel,
    val options: Options,
    val canSelect: Boolean,
    val isSelecting: Boolean,
    val selectingAll: Boolean,
    val multiSelectedPaths: Set<String>,
) {
    @Immutable
    data class Options(
        val withFoldersSize: Boolean,
    )
}

fun previewFilesStateUiModel() = FilesStateUiModel(
    current = previewFileUiModel(
        name = "Caches",
    ),
    backStack = emptyList(),
    files = listOf(
        previewFileUiModel(
            name = "Caches",
        ),
        previewFileUiModel(
            name = "Files",
        ),
    ),
    headerState = previewFilesHeaderStateUiModel(),
    options = FilesStateUiModel.Options(
        withFoldersSize = false,
    ),
    isSelecting = false,
    multiSelectedPaths = emptySet(),
    selectingAll = false,
    numberOfFiles = null,
    canSelect = false,
)

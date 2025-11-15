package io.github.openflocon.flocondesktop.features.files.model

import androidx.compose.runtime.Immutable

@Immutable
data class FilesStateUiModel(
    val backStack: List<FileUiModel>,
    val current: FileUiModel?,
    val files: List<FileUiModel>,
    val headerState: FilesHeaderStateUiModel,
)

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
)

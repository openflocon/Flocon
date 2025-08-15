package io.github.openflocon.flocondesktop.features.files.model

data class FilesStateUiModel(
    val backStack: List<FileUiModel>,
    val current: FileUiModel?,
    val files: List<FileUiModel>,
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
)

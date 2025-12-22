package io.github.openflocon.flocondesktop.features.files.model

import androidx.compose.runtime.Immutable

@Immutable
data class FilesStateUiModel(
    val backStack: List<FileUiModel>,
    val current: FileUiModel?,
    val files: List<FileUiModel>,
    val headerState: FilesHeaderStateUiModel,
    val options: Options,
    val multiSelection: MultiSelection = MultiSelection(),
) {
    @Immutable
    data class Options(
        val withFoldersSize: Boolean,
    )

    @Immutable
    data class MultiSelection(
        val enabled: Boolean = false,
        val selectedPaths: Set<String> = emptySet(),
        val lastSelectedPath: String? = null,
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
    )
)

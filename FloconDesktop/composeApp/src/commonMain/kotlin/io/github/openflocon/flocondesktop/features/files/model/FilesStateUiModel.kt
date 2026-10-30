package io.github.openflocon.flocondesktop.features.files.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

@Immutable
data class FilesStateUiModel(
    val backStack: ImmutableList<FileUiModel>,
    val current: FileUiModel?,
    val files: ImmutableList<FileUiModel>,
    val headerState: FilesHeaderStateUiModel,
    val footer: FilesFooterUiModel,
    val options: Options,
    val viewMode: FilesViewModeUiModel,
    val canSelect: Boolean,
    val isSelecting: Boolean,
    val selectingAll: Boolean,
    val multiSelectedPaths: ImmutableSet<String>,
    // on the root, when the app announced no folder
    val showEmptyState: Boolean = false,
) {
    @Immutable
    data class Options(
        val withFoldersSize: Boolean,
    )
}

/** Bottom bar of the list: counts, total size and the path of the folder on the device. */
@Immutable
data class FilesFooterUiModel(
    val itemCount: Int,
    val folderCount: Int,
    // absolute path of the current folder on the device, null on the root screen or when unknown
    val path: String?,
)

fun previewFilesStateUiModel(
    viewMode: FilesViewModeUiModel = FilesViewModeUiModel.List,
) = FilesStateUiModel(
    current = previewFileUiModel(name = "datastore"),
    backStack = persistentListOf(previewFileUiModel(name = "Files")),
    files = previewFileUiModels(),
    headerState = previewFilesHeaderStateUiModel(),
    footer = FilesFooterUiModel(
        itemCount = 7,
        folderCount = 2,
        path = "/data/user/0/io.openflocon.sample/files/datastore",
    ),
    options = FilesStateUiModel.Options(
        withFoldersSize = false,
    ),
    viewMode = viewMode,
    isSelecting = false,
    multiSelectedPaths = persistentSetOf(),
    selectingAll = false,
    canSelect = true,
)

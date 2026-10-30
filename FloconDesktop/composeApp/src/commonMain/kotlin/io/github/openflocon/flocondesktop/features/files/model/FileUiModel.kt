package io.github.openflocon.flocondesktop.features.files.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class FileUiModel(
    val name: String,
    val type: FileTypeUiModel,
    val path: FilePathUiModel,
    // short label of the extension displayed on the tile / badge of a file ("{ }", "PB", "PNG"...), empty for a folder
    val extensionLabel: String,
    val dateFormatted: String?,
    // null when unknown (folders without the "folder sizes" option, roots)
    val sizeFormatted: String?,
    // share of the total size of the displayed entries, between 0 and 1, null when the size is not displayed
    val sizeRatio: Float?,
    // the same share as a percentage for the tooltip of the bar ("61%", "<1%")
    val sizeShareFormatted: String?,
    val contextualActions: ImmutableList<ContextualAction>,
) {

    @Immutable
    data class ContextualAction(
        val id: Action,
        val text: String,
    ) {
        enum class Action {
            Open,
            Delete,
            DeleteContent,
            CopyPath,
        }
    }

    val isDirectory = type == FileTypeUiModel.Folder

    // unique in a folder, used as a key by the lazy lists
    val id: String = when (path) {
        is FilePathUiModel.Real -> path.absolutePath
        is FilePathUiModel.Root -> path.id
    }
}

fun previewFileUiModel(
    name: String,
    type: FileTypeUiModel = FileTypeUiModel.Folder,
    extensionLabel: String = "",
    sizeFormatted: String? = "100 MB",
    sizeRatio: Float? = 0.5f,
) = FileUiModel(
    name = name,
    type = type,
    path = FilePathUiModel.Real(absolutePath = "/data/user/0/io.openflocon.sample/files/$name"),
    extensionLabel = extensionLabel,
    sizeFormatted = sizeFormatted,
    sizeRatio = sizeRatio,
    sizeShareFormatted = sizeRatio?.let { "${(it * 100).toInt()}%" },
    contextualActions = persistentListOf(),
    dateFormatted = "30/10/2026 11:53:12",
)

fun previewFileUiModels(): ImmutableList<FileUiModel> = persistentListOf(
    previewFileUiModel(name = "preferences_pb", sizeFormatted = null, sizeRatio = null),
    previewFileUiModel(name = "crash_logs", sizeFormatted = null, sizeRatio = null),
    previewFileUiModel(
        name = "user_settings.preferences_pb",
        type = FileTypeUiModel.Proto,
        extensionLabel = "PB",
        sizeFormatted = "4.1 KB",
        sizeRatio = 1f,
    ),
    previewFileUiModel(
        name = "flocon_network_mocks.json",
        type = FileTypeUiModel.Json,
        extensionLabel = "{ }",
        sizeFormatted = "2.0 KB",
        sizeRatio = 0.5f,
    ),
    previewFileUiModel(
        name = "avatar_cache.png",
        type = FileTypeUiModel.Image,
        extensionLabel = "PNG",
        sizeFormatted = "3.1 KB",
        sizeRatio = 0.75f,
    ),
    previewFileUiModel(
        name = "app.db",
        type = FileTypeUiModel.Database,
        extensionLabel = "DB",
        sizeFormatted = "1.2 KB",
        sizeRatio = 0.3f,
    ),
    previewFileUiModel(
        name = "profileInstalled",
        type = FileTypeUiModel.Text,
        extensionLabel = "FILE",
        sizeFormatted = "24 B",
        sizeRatio = 0.01f,
    ),
)

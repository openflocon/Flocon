package io.github.openflocon.flocondesktop.features.files.ui.model

import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.folder
import org.jetbrains.compose.resources.DrawableResource

data class FileUiModel(
    val name: String,
    val type: FileTypeUiModel,
    val path: FilePathUiModel,
    val icon: DrawableResource,
    val size: Long,
    val contextualActions: List<ContextualAction>,
) {

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
}

fun previewFileUiModel(
    name: String,
    type: FileTypeUiModel = FileTypeUiModel.Folder,
) = FileUiModel(
    name = name,
    type = type,
    path = FilePathUiModel.Constants.CachesDir,
    size = 100L,
    icon = Res.drawable.folder,
    contextualActions = listOf(),
)

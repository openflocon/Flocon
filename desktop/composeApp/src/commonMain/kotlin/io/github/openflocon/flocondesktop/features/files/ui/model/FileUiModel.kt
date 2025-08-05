package io.github.openflocon.flocondesktop.features.files.ui.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.ui.graphics.vector.ImageVector

data class FileUiModel(
    val name: String,
    val type: FileTypeUiModel,
    val path: FilePathUiModel,
    val icon: ImageVector,
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
    icon = Icons.Outlined.Folder,
    contextualActions = listOf(),
)

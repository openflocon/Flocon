package io.github.openflocon.flocondesktop.features.files.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector

@Immutable
data class FileUiModel(
    val name: String,
    val type: FileTypeUiModel,
    val path: FilePathUiModel,
    val icon: ImageVector,
    val dateFormatted: String?,
    val sizeFormatted: String?,
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
    sizeFormatted = "100 MB",
    icon = Icons.Outlined.Folder,
    contextualActions = listOf(),
    dateFormatted = "2022-01-01 12:10",
)

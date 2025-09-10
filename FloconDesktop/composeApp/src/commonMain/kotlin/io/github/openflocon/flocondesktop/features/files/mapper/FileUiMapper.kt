package io.github.openflocon.flocondesktop.features.files.mapper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Drafts
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.openflocon.domain.files.models.FileDomainModel
import io.github.openflocon.domain.files.models.FilePathDomainModel
import io.github.openflocon.flocondesktop.features.files.model.FilePathUiModel
import io.github.openflocon.flocondesktop.features.files.model.FileTypeUiModel
import io.github.openflocon.flocondesktop.features.files.model.FileUiModel
import kotlin.time.Instant

fun FileUiModel.icon(): ImageVector = if (this.isDirectory) Icons.Outlined.Folder else Icons.Outlined.Drafts

fun FileDomainModel.toUi(): FileUiModel = FileUiModel(
    name = name,
    type = if (this.isDirectory) {
        FileTypeUiModel.Folder
    } else {
        FileTypeUiModel.Other // TODO
    },
    path = path.toUi(),
    size = size,
    icon = if (this.isDirectory) {
        Icons.Outlined.Folder
    } else {
        Icons.Outlined.Description
    },
    contextualActions = buildContextualActions(
        isConstant = this.path is FilePathDomainModel.Constants,
        isFolder = this.isDirectory,
    ),
)

fun FileUiModel.toDomain(): FileDomainModel = FileDomainModel(
    name = name,
    isDirectory = isDirectory,
    path = path.toDomain(),
    size = size,
    lastModified = Instant.fromEpochMilliseconds(0L), // TODO
)

fun FilePathUiModel.toDomain(): FilePathDomainModel = when (this) {
    FilePathUiModel.Constants.CachesDir -> FilePathDomainModel.Constants.CachesDir
    FilePathUiModel.Constants.FilesDir -> FilePathDomainModel.Constants.FilesDir
    is FilePathUiModel.Real -> FilePathDomainModel.Real(this.absolutePath)
}

fun FilePathDomainModel.toUi(): FilePathUiModel = when (this) {
    FilePathDomainModel.Constants.CachesDir -> FilePathUiModel.Constants.CachesDir
    FilePathDomainModel.Constants.FilesDir -> FilePathUiModel.Constants.FilesDir
    is FilePathDomainModel.Real -> FilePathUiModel.Real(this.absolutePath)
}

fun buildContextualActions(
    isFolder: Boolean,
    isConstant: Boolean,
): List<FileUiModel.ContextualAction> {
    val contextualActions = mutableListOf<FileUiModel.ContextualAction>()
    contextualActions.add(
        FileUiModel.ContextualAction(
            id = FileUiModel.ContextualAction.Action.Open,
            text = "Open",
        ),
    )
    if (!isConstant) {
        contextualActions.add(
            FileUiModel.ContextualAction(
                id = FileUiModel.ContextualAction.Action.CopyPath,
                text = "Copy Path",
            ),
        )
    }
    if (isFolder) {
        contextualActions.add(
            FileUiModel.ContextualAction(
                id = FileUiModel.ContextualAction.Action.DeleteContent,
                text = "Delete Content",
            ),
        )
    }
    if (!isConstant) {
        contextualActions.add(
            FileUiModel.ContextualAction(
                id = FileUiModel.ContextualAction.Action.Delete,
                text = "Delete",
            ),
        )
    }
    return contextualActions
}

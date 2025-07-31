package io.github.openflocon.flocondesktop.features.files.ui.mapper

import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.file
import flocondesktop.composeapp.generated.resources.folder
import io.github.openflocon.flocondesktop.features.files.domain.model.FileDomainModel
import io.github.openflocon.flocondesktop.features.files.domain.model.FilePathDomainModel
import io.github.openflocon.flocondesktop.features.files.ui.model.FilePathUiModel
import io.github.openflocon.flocondesktop.features.files.ui.model.FileTypeUiModel
import io.github.openflocon.flocondesktop.features.files.ui.model.FileUiModel
import io.github.openflocon.flocondesktop.features.files.ui.model.FileUiModel.ContextualAction
import org.jetbrains.compose.resources.DrawableResource
import kotlin.time.Instant

fun FileUiModel.icon(): DrawableResource = if (this.isDirectory) Res.drawable.folder else Res.drawable.file

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
        Res.drawable.folder
    } else {
        Res.drawable.file
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
): List<ContextualAction> {
    val contextualActions = mutableListOf<ContextualAction>()
    contextualActions.add(
        ContextualAction(
            id = ContextualAction.Action.Open,
            text = "Open",
        ),
    )
    if (!isConstant) {
        contextualActions.add(
            ContextualAction(
                id = ContextualAction.Action.CopyPath,
                text = "Copy Path",
            ),
        )
    }
    if (isFolder) {
        contextualActions.add(
            ContextualAction(
                id = ContextualAction.Action.DeleteContent,
                text = "Delete Content",
            ),
        )
    }
    if (!isConstant) {
        contextualActions.add(
            ContextualAction(
                id = ContextualAction.Action.Delete,
                text = "Delete",
            ),
        )
    }
    return contextualActions
}

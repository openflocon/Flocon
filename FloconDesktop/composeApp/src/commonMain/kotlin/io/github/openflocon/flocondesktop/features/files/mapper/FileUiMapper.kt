package io.github.openflocon.flocondesktop.features.files.mapper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Drafts
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.openflocon.domain.common.ByteFormatter
import io.github.openflocon.domain.files.models.FileDomainModel
import io.github.openflocon.domain.files.models.FilePathDomainModel
import io.github.openflocon.domain.files.models.FileRootDomainModel
import io.github.openflocon.flocondesktop.features.files.model.FilePathUiModel
import io.github.openflocon.flocondesktop.features.files.model.FileTypeUiModel
import io.github.openflocon.flocondesktop.features.files.model.FileUiModel
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.Instant
import kotlin.time.toJavaInstant
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

fun FileUiModel.icon(): ImageVector = if (this.isDirectory) Icons.Outlined.Folder else Icons.Outlined.Drafts

fun FileDomainModel.toUi(withFoldersSize: Boolean): FileUiModel = FileUiModel(
    name = name,
    type = if (this.isDirectory) {
        FileTypeUiModel.Folder
    } else {
        FileTypeUiModel.Other // TODO
    },
    path = path.toUi(),
    sizeFormatted = if (this.isDirectory && withFoldersSize.not()) null else ByteFormatter.formatBytes(size),
    icon = if (this.isDirectory) {
        Icons.Outlined.Folder
    } else {
        Icons.Outlined.Description
    },
    dateFormatted = formatDate(lastModified),
    contextualActions = buildContextualActions(
        isConstant = this.path is FilePathDomainModel.Root,
        isFolder = this.isDirectory,
    ),
)

private fun formatDate(date: Instant): String? = try {
    val formateur = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        .withZone(ZoneId.systemDefault())

    formateur.format(date.toJavaInstant())
} catch (t: Throwable) {
    t.printStackTrace()
    null
}

fun FileUiModel.toDomain(): FileDomainModel = FileDomainModel(
    name = name,
    isDirectory = isDirectory,
    path = path.toDomain(),
    size = 0L, // not useful
    lastModified = Instant.fromEpochMilliseconds(0L), // TODO
)

fun FilePathUiModel.toDomain(): FilePathDomainModel = when (this) {
    is FilePathUiModel.Root -> FilePathDomainModel.Root(this.id)
    is FilePathUiModel.Real -> FilePathDomainModel.Real(this.absolutePath)
}

fun FilePathDomainModel.toUi(): FilePathUiModel = when (this) {
    is FilePathDomainModel.Root -> FilePathUiModel.Root(this.id)
    is FilePathDomainModel.Real -> FilePathUiModel.Real(this.absolutePath)
}

fun FileRootDomainModel.toUi(): FileUiModel = FileUiModel(
    name = name,
    type = FileTypeUiModel.Folder,
    path = FilePathUiModel.Root(id),
    sizeFormatted = null,
    icon = Icons.Outlined.Folder,
    dateFormatted = null,
    contextualActions = buildContextualActions(
        isConstant = true,
        isFolder = true,
    ),
)

fun buildContextualActions(
    isFolder: Boolean,
    isConstant: Boolean,
): ImmutableList<FileUiModel.ContextualAction> {
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
    return contextualActions.toImmutableList()
}

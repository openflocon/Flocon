package io.github.openflocon.flocondesktop.features.files.mapper

import io.github.openflocon.domain.common.ByteFormatter
import io.github.openflocon.domain.files.models.FileDomainModel
import io.github.openflocon.domain.files.models.FilePathDomainModel
import io.github.openflocon.domain.files.models.FileRootDomainModel
import io.github.openflocon.flocondesktop.features.files.model.FilePathUiModel
import io.github.openflocon.flocondesktop.features.files.model.FileTypeUiModel
import io.github.openflocon.flocondesktop.features.files.model.FileUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.Instant
import kotlin.time.toJavaInstant

// the smallest visible part of the size bar, so that a tiny file still shows something
private const val MinSizeRatio = 0.01f
private const val MaxExtensionLabelLength = 4
private const val NoExtensionLabel = "FILE"

fun FileDomainModel.toUi(
    withFoldersSize: Boolean,
    maxSize: Long? = null,
): FileUiModel {
    val type = fileTypeOf(name = name, isDirectory = isDirectory)
    val showSize = !isDirectory || withFoldersSize
    return FileUiModel(
        name = name,
        type = type,
        path = path.toUi(),
        extensionLabel = extensionLabelOf(name = name, type = type),
        sizeFormatted = if (showSize) ByteFormatter.formatBytes(size) else null,
        sizeRatio = if (showSize) sizeRatio(size = size, maxSize = maxSize) else null,
        dateFormatted = formatDate(lastModified),
        contextualActions = buildContextualActions(
            isConstant = this.path is FilePathDomainModel.Root,
            isFolder = this.isDirectory,
        ),
    )
}

/** Maps the content of a folder, the size bars are relative to its biggest displayed entry. */
fun List<FileDomainModel>.toUi(withFoldersSize: Boolean): ImmutableList<FileUiModel> {
    val maxSize = this
        .filter { !it.isDirectory || withFoldersSize }
        .maxOfOrNull { it.size }
    return map { it.toUi(withFoldersSize = withFoldersSize, maxSize = maxSize) }.toImmutableList()
}

internal fun sizeRatio(size: Long, maxSize: Long?): Float? = when {
    maxSize == null -> null
    maxSize <= 0L -> MinSizeRatio
    else -> (size.toFloat() / maxSize).coerceIn(MinSizeRatio, 1f)
}

// "a.json" -> "json", none for "profileInstalled", ".hidden" or "trailing."
internal fun extensionOf(name: String): String? {
    val dot = name.lastIndexOf('.')
    return if (dot <= 0 || dot == name.lastIndex) null else name.substring(dot + 1).lowercase()
}

internal fun fileTypeOf(name: String, isDirectory: Boolean): FileTypeUiModel {
    if (isDirectory) return FileTypeUiModel.Folder
    return when (extensionOf(name)) {
        "json" -> FileTypeUiModel.Json
        "pb", "proto", "preferences_pb" -> FileTypeUiModel.Proto
        "db", "sqlite", "sqlite3", "db3", "db-wal", "db-shm", "db-journal" -> FileTypeUiModel.Database
        "png", "jpg", "jpeg", "gif", "webp", "bmp", "heic", "svg" -> FileTypeUiModel.Image
        "mp4", "mov", "webm", "mkv", "3gp", "avi" -> FileTypeUiModel.Video
        "txt", "log", "xml", "csv", "md", "yml", "yaml", "properties", "html" -> FileTypeUiModel.Text
        else -> FileTypeUiModel.Other
    }
}

internal fun extensionLabelOf(name: String, type: FileTypeUiModel): String = when (type) {
    FileTypeUiModel.Folder -> ""

    FileTypeUiModel.Json -> "{ }"

    FileTypeUiModel.Proto -> "PB"

    FileTypeUiModel.Database -> "DB"

    FileTypeUiModel.Image,
    FileTypeUiModel.Video,
    FileTypeUiModel.Text,
    FileTypeUiModel.Other,
    -> extensionOf(name)?.uppercase()?.take(MaxExtensionLabelLength) ?: NoExtensionLabel
}

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
    extensionLabel = "",
    sizeFormatted = null,
    sizeRatio = null,
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

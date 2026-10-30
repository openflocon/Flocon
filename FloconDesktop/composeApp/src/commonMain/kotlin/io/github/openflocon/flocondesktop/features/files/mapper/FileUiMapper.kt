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
import kotlin.math.roundToInt
import kotlin.time.Instant
import kotlin.time.toJavaInstant

// the smallest visible part of the size bar (~2px), so that a tiny but not empty file still shows something
private const val MinSizeRatio = 0.02f
private const val MaxExtensionLabelLength = 4
private const val NoExtensionLabel = "FILE"
private const val EllipsisPrefix = "…"

fun FileDomainModel.toUi(
    withFoldersSize: Boolean,
    totalSize: Long? = null,
): FileUiModel {
    val type = fileTypeOf(name = name, isDirectory = isDirectory)
    val showSize = !isDirectory || withFoldersSize
    return FileUiModel(
        name = name,
        type = type,
        path = path.toUi(),
        extensionLabel = extensionLabelOf(name = name, type = type),
        sizeFormatted = if (showSize) ByteFormatter.formatBytes(size) else null,
        sizeRatio = if (showSize) sizeRatio(size = size, totalSize = totalSize) else null,
        sizeShareFormatted = if (showSize) sizeShareFormatted(size = size, totalSize = totalSize) else null,
        dateFormatted = formatDate(lastModified),
        contextualActions = buildContextualActions(
            isConstant = this.path is FilePathDomainModel.Root,
            isFolder = this.isDirectory,
        ),
    )
}

/**
 * Maps the content of a folder, the size bars show the share of each entry in the total size of the displayed ones
 * (only the files when the folder sizes are not computed).
 */
fun List<FileDomainModel>.toUi(withFoldersSize: Boolean): ImmutableList<FileUiModel> {
    val sized = filter { !it.isDirectory || withFoldersSize }
    val totalSize = if (sized.isEmpty()) null else sized.sumOf { it.size }
    return map { it.toUi(withFoldersSize = withFoldersSize, totalSize = totalSize) }.toImmutableList()
}

internal fun sizeRatio(size: Long, totalSize: Long?): Float? = when {
    totalSize == null -> null
    size <= 0L || totalSize <= 0L -> 0f
    else -> (size.toFloat() / totalSize).coerceIn(MinSizeRatio, 1f)
}

internal fun sizeShareFormatted(size: Long, totalSize: Long?): String? {
    if (totalSize == null || totalSize <= 0L) return null
    val percent = size * 100.0 / totalSize
    return when {
        size <= 0L -> "0%"
        percent < 1.0 -> "<1%"
        else -> "${percent.roundToInt()}%"
    }
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

/**
 * Removes the folder of the app on the device from [path]: the folder all the [rootPaths] announced by the app share
 * ("/data/user/0/com.app/files/datastore" -> "…/files/datastore"), or the parent of the root when there is only one.
 * The path is returned as is when it is not inside that folder.
 */
internal fun shortDevicePath(path: String, rootPaths: List<String>): String {
    val roots = rootPaths.filter { it.isNotBlank() }.map { it.trimEnd('/') }
    val appFolder = when (roots.size) {
        0 -> return path
        1 -> roots.single().substringBeforeLast('/', missingDelimiterValue = "")
        else -> commonFolderOf(roots)
    }
    return when {
        appFolder.isEmpty() -> path
        path == appFolder -> "$EllipsisPrefix/${appFolder.substringAfterLast('/')}"
        path.startsWith("$appFolder/") -> EllipsisPrefix + path.removePrefix(appFolder)
        else -> path
    }
}

// longest folder shared by all the paths, "" when they only share "/"
private fun commonFolderOf(paths: List<String>): String = paths
    .map { it.split('/') }
    .reduce { common, segments -> common.zip(segments).takeWhile { (a, b) -> a == b }.map { it.first } }
    .joinToString("/")

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
    sizeShareFormatted = null,
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

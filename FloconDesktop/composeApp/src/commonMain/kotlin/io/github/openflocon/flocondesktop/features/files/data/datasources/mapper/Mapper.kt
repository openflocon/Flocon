package io.github.openflocon.flocondesktop.features.files.data.datasources

import io.github.openflocon.flocondesktop.features.files.data.datasources.model.FileEntity
import com.flocon.library.domain.models.FileDomainModel
import com.flocon.library.domain.models.FilePathDomainModel
import kotlin.time.Instant

internal fun FilePathDomainModel.mapToLocal(): String = when (this) {
    is FilePathDomainModel.Constants.CachesDir -> "___[" + "cache" + "]___"
    is FilePathDomainModel.Constants.FilesDir -> "___[" + "files" + "]___"
    is FilePathDomainModel.Real -> this.absolutePath
}

internal fun pathFromLocal(path: String): FilePathDomainModel = if (path.startsWith("___[") && path.endsWith("]___")) {
    val constantPath = path.removeSurrounding("___[", "]___")
    when (constantPath) {
        "cache" -> FilePathDomainModel.Constants.CachesDir
        "files" -> FilePathDomainModel.Constants.FilesDir
        else -> FilePathDomainModel.Real(constantPath)
    }
} else {
    FilePathDomainModel.Real(path)
}

fun FileEntity.toDomainModel(): FileDomainModel = FileDomainModel(
    name = name,
    isDirectory = isDirectory,
    path = pathFromLocal(path), // Utilisation de pathFromLocal
    size = size,
    lastModified = Instant.fromEpochMilliseconds(lastModifiedTimestamp),
)

fun FileDomainModel.toEntity(
    deviceId: String,
    packageName: String,
    parentFilePath: FilePathDomainModel,
): FileEntity = FileEntity(
    deviceId = deviceId,
    packageName = packageName,
    name = name,
    isDirectory = isDirectory,
    path = path.mapToLocal(),
    parentPath = parentFilePath.mapToLocal(),
    size = size,
    lastModifiedTimestamp = lastModified.toEpochMilliseconds(),
)

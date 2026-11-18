package io.github.openflocon.data.local.files.mapper

import io.github.openflocon.data.local.files.models.FileEntity
import io.github.openflocon.domain.files.models.FileDomainModel
import io.github.openflocon.domain.files.models.FilePathDomainModel
import kotlin.time.Instant

internal fun FilePathDomainModel.mapToLocal(): String = when (this) {
    is FilePathDomainModel.Root -> "___[" + id + "]___"
    is FilePathDomainModel.Real -> this.absolutePath
}

internal fun pathFromLocal(path: String): FilePathDomainModel = if (path.startsWith("___[") && path.endsWith("]___")) {
    FilePathDomainModel.Root(id = path.removeSurrounding("___[", "]___"))
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

package io.github.openflocon.flocondesktop.features.files.domain.repository

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.common.Either
import io.github.openflocon.flocondesktop.features.files.domain.model.FileDomainModel
import io.github.openflocon.flocondesktop.features.files.domain.model.FilePathDomainModel
import kotlinx.coroutines.flow.Flow

interface FilesRepository {
    fun observeFolderContent(
        deviceId: DeviceId,
        path: FilePathDomainModel,
    ): Flow<List<FileDomainModel>>

    suspend fun refreshFolderContent(
        deviceId: DeviceId,
        path: FilePathDomainModel,
    ): Either<Throwable, Unit>

    suspend fun deleteFolderContent(
        deviceId: DeviceId,
        path: FilePathDomainModel,
    ): Either<Throwable, Unit>

    suspend fun deleteFile(
        deviceId: DeviceId,
        parentPath: FilePathDomainModel,
        path: FilePathDomainModel,
    ): Either<Throwable, Unit>
}

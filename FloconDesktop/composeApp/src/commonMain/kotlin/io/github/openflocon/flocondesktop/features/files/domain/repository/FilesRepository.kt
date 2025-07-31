package io.github.openflocon.flocondesktop.features.files.domain.repository

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.common.Either
import com.florent37.flocondesktop.features.files.domain.model.FileDomainModel
import com.florent37.flocondesktop.features.files.domain.model.FilePathDomainModel
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

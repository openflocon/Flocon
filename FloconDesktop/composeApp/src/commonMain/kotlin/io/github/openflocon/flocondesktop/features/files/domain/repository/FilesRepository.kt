package io.github.openflocon.flocondesktop.features.files.domain.repository

import io.github.openflocon.flocondesktop.common.Either
import io.github.openflocon.flocondesktop.features.files.domain.model.FileDomainModel
import io.github.openflocon.flocondesktop.features.files.domain.model.FilePathDomainModel
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageName
import kotlinx.coroutines.flow.Flow

interface FilesRepository {
    fun observeFolderContent(
        deviceIdAndPackageName: DeviceIdAndPackageName,
        path: FilePathDomainModel,
    ): Flow<List<FileDomainModel>>

    suspend fun refreshFolderContent(
        deviceIdAndPackageName: DeviceIdAndPackageName,
        path: FilePathDomainModel,
    ): Either<Throwable, Unit>

    suspend fun deleteFolderContent(
        deviceIdAndPackageName: DeviceIdAndPackageName,
        path: FilePathDomainModel,
    ): Either<Throwable, Unit>

    suspend fun deleteFile(
        deviceIdAndPackageName: DeviceIdAndPackageName,
        parentPath: FilePathDomainModel,
        path: FilePathDomainModel,
    ): Either<Throwable, Unit>
}

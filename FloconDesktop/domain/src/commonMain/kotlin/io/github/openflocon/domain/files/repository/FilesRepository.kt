package io.github.openflocon.domain.files.repository

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.files.models.FileDomainModel
import io.github.openflocon.domain.files.models.FilePathDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.messages.models.FloconReceivedFileDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface FilesRepository {

    fun observeFolderContent(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        path: FilePathDomainModel,
    ): Flow<List<FileDomainModel>>

    suspend fun refreshFolderContent(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        path: FilePathDomainModel,
    ): Either<Throwable, Unit>

    suspend fun deleteFolderContent(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        path: FilePathDomainModel,
    ): Either<Throwable, Unit>

    suspend fun deleteFile(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        parentPath: FilePathDomainModel,
        path: FilePathDomainModel,
    ): Either<Throwable, Unit>

    suspend fun downloadFile(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        path: String
    ) : Either<Throwable, FloconReceivedFileDomainModel>

    fun observeWithFoldersSize(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<Boolean>

    suspend fun saveWithFoldersSize(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, value: Boolean)
}

package io.github.openflocon.data.core.files.datasource

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.files.models.FileDomainModel
import io.github.openflocon.domain.files.models.FilePathDomainModel
import io.github.openflocon.domain.files.models.FromDeviceFilesResultDomainModel
import kotlin.uuid.ExperimentalUuidApi

interface FilesRemoteDataSource {

    fun onGetFilesResultReceived(received: FromDeviceFilesResultDomainModel)

    @OptIn(ExperimentalUuidApi::class)
    suspend fun executeGetFile(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        path: FilePathDomainModel,
    ): Either<Throwable, List<FileDomainModel>>

    suspend fun executeDeleteFolderContent(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        folderPath: FilePathDomainModel,
    ): Either<Exception, List<FileDomainModel>>

    suspend fun executeDeleteFile(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        folderPath: FilePathDomainModel,
        filePath: FilePathDomainModel,
    ): Either<Exception, List<FileDomainModel>>

}

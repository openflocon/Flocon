package io.github.openflocon.data.core.files.datasource

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.files.models.FileDomainModel
import io.github.openflocon.domain.files.models.FilePathDomainModel
import io.github.openflocon.domain.files.models.FromDeviceFilesResultDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.messages.models.FloconReceivedFileDomainModel
import kotlin.uuid.ExperimentalUuidApi

interface FilesRemoteDataSource {

    fun onGetFilesResultReceived(received: FromDeviceFilesResultDomainModel)
    fun onFloconReceivedFilesDomainModel(received: FloconReceivedFileDomainModel)

    @OptIn(ExperimentalUuidApi::class)
    suspend fun executeListFiles(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        path: FilePathDomainModel,
        withFoldersSize: Boolean,
    ): Either<Throwable, List<FileDomainModel>>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun executeDownloadFile(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        path: String,
    ): Either<Throwable, FloconReceivedFileDomainModel>

    suspend fun executeDeleteFolderContent(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        folderPath: FilePathDomainModel,
    ): Either<Exception, List<FileDomainModel>>

    suspend fun executeDeleteFile(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        folderPath: FilePathDomainModel,
        filePath: FilePathDomainModel,
    ): Either<Exception, List<FileDomainModel>>

    suspend fun executeDeleteFiles(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        folderPath: FilePathDomainModel,
        filePaths: List<FilePathDomainModel>,
    ): Either<Exception, List<FileDomainModel>>

    fun getItems(message: FloconIncomingMessageDomainModel): FromDeviceFilesResultDomainModel?
}

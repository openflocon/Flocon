package io.github.openflocon.data.core.files.repository

import io.github.openflocon.data.core.files.datasource.FilesLocalDataSource
import io.github.openflocon.data.core.files.datasource.FilesRemoteDataSource
import io.github.openflocon.domain.Protocol
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.files.models.FileDomainModel
import io.github.openflocon.domain.files.models.FilePathDomainModel
import io.github.openflocon.domain.files.repository.FilesRepository
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.messages.models.FloconReceivedFileDomainModel
import io.github.openflocon.domain.messages.repository.FileReceiverRepository
import io.github.openflocon.domain.messages.repository.MessagesReceiverRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class FilesRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val localFilesDataSource: FilesLocalDataSource,
    private val remoteFilesDataSource: FilesRemoteDataSource,
) : FilesRepository,
    MessagesReceiverRepository,
    FileReceiverRepository {

    override val pluginName = listOf(Protocol.FromDevice.Files.Plugin)

    override suspend fun onMessageReceived(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        message: FloconIncomingMessageDomainModel,
    ) {
        withContext(dispatcherProvider.data) {
            when (message.method) {
                Protocol.FromDevice.Files.Method.ListFiles -> remoteFilesDataSource.getItems(message)
                    ?.let(remoteFilesDataSource::onGetFilesResultReceived)
            }
        }
    }

    override suspend fun onFileReceived(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        receivedFile: FloconReceivedFileDomainModel
    ) {
        remoteFilesDataSource.onFloconReceivedFilesDomainModel(receivedFile)
    }

    override suspend fun onDeviceConnected(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        isNewDevice: Boolean,
    ) {
        // no op
    }


    override fun observeFolderContent(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        path: FilePathDomainModel,
    ): Flow<List<FileDomainModel>> = localFilesDataSource
        .observeFolderContentUseCase(
            deviceIdAndPackageName = deviceIdAndPackageName,
            folderPath = path,
        ).flowOn(dispatcherProvider.data)

    override suspend fun refreshFolderContent(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        path: FilePathDomainModel,
    ): Either<Throwable, Unit> = withContext(dispatcherProvider.data) {
        remoteFilesDataSource
            .executeListFiles(
                deviceIdAndPackageName = deviceIdAndPackageName,
                path = path,
                withFoldersSize = observeWithFoldersSize(deviceIdAndPackageName).firstOrNull() ?: false,
            ).alsoSuccess {
                localFilesDataSource.storeFiles(
                    deviceIdAndPackageName = deviceIdAndPackageName,
                    parentPath = path,
                    files = it,
                )
                // store the result
            }.mapSuccess { }
    }

    override suspend fun downloadFile(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        path: String,
    ) = withContext(dispatcherProvider.data) {
        remoteFilesDataSource
            .executeDownloadFile(
                deviceIdAndPackageName = deviceIdAndPackageName,
                path = path,
            )
    }

    override suspend fun deleteFolderContent(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        path: FilePathDomainModel,
    ): Either<Throwable, Unit> = withContext(dispatcherProvider.data) {
        remoteFilesDataSource
            .executeDeleteFolderContent(
                deviceIdAndPackageName = deviceIdAndPackageName,
                folderPath = path,
            ).alsoSuccess {
                localFilesDataSource.storeFiles(
                    deviceIdAndPackageName = deviceIdAndPackageName,
                    parentPath = path,
                    files = it,
                )
                // store the result
            }.mapSuccess { }
    }

    override suspend fun deleteFile(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        parentPath: FilePathDomainModel,
        path: FilePathDomainModel,
    ): Either<Throwable, Unit> = withContext(dispatcherProvider.data) {
        remoteFilesDataSource
            .executeDeleteFile(
                deviceIdAndPackageName = deviceIdAndPackageName,
                filePath = path,
                folderPath = parentPath,
            ).alsoSuccess {
                localFilesDataSource.storeFiles(
                    deviceIdAndPackageName = deviceIdAndPackageName,
                    parentPath = parentPath,
                    files = it,
                )
                // store the result
            }.mapSuccess { }
    }

    override fun observeWithFoldersSize(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<Boolean> {
        return localFilesDataSource.observeWithFoldersSize(deviceIdAndPackageName)
            .flowOn(dispatcherProvider.data)
    }

    override suspend fun saveWithFoldersSize(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        value: Boolean
    ) {
        withContext(dispatcherProvider.data) {
            localFilesDataSource.saveWithFoldersSize(
                deviceIdAndPackageName = deviceIdAndPackageName,
                value = value
            )
        }
    }
}

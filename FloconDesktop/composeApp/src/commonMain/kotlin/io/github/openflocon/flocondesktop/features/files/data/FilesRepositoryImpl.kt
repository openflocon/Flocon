package io.github.openflocon.flocondesktop.features.files.data

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.FloconIncomingMessageDataModel
import com.florent37.flocondesktop.Protocol
import com.florent37.flocondesktop.common.Either
import com.florent37.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import com.florent37.flocondesktop.features.files.data.datasources.LocalFilesDataSource
import com.florent37.flocondesktop.features.files.data.datasources.RemoteFilesDataSource
import com.florent37.flocondesktop.features.files.data.mapper.decodeListFilesResult
import com.florent37.flocondesktop.features.files.domain.model.FileDomainModel
import com.florent37.flocondesktop.features.files.domain.model.FilePathDomainModel
import com.florent37.flocondesktop.features.files.domain.repository.FilesRepository
import com.florent37.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class FilesRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val localFilesDataSource: LocalFilesDataSource,
    private val remoteFilesDataSource: RemoteFilesDataSource,
) : FilesRepository,
    MessagesReceiverRepository {

    override val pluginName = listOf(Protocol.FromDevice.Files.Plugin)

    override suspend fun onMessageReceived(
        deviceId: String,
        message: FloconIncomingMessageDataModel,
    ) {
        withContext(dispatcherProvider.data) {
            when (message.method) {
                Protocol.FromDevice.Files.Method.ListFiles ->
                    decodeListFilesResult(message.body)
                        ?.let { received ->
                            remoteFilesDataSource.onGetFilesResultReceived(
                                received = received,
                            )
                        }
            }
        }
    }

    override fun observeFolderContent(
        deviceId: DeviceId,
        path: FilePathDomainModel,
    ): Flow<List<FileDomainModel>> = localFilesDataSource
        .observeFolderContentUseCase(
            deviceId = deviceId,
            folderPath = path,
        ).flowOn(dispatcherProvider.data)

    override suspend fun refreshFolderContent(
        deviceId: DeviceId,
        path: FilePathDomainModel,
    ): Either<Throwable, Unit> = withContext(dispatcherProvider.data) {
        remoteFilesDataSource
            .executeGetFile(
                deviceId = deviceId,
                path = path,
            ).alsoSuccess {
                localFilesDataSource.storeFiles(
                    deviceId = deviceId,
                    parentPath = path,
                    files = it,
                )
                // store the result
            }.mapSuccess { }
    }

    override suspend fun deleteFolderContent(
        deviceId: DeviceId,
        path: FilePathDomainModel,
    ): Either<Throwable, Unit> = withContext(dispatcherProvider.data) {
        remoteFilesDataSource
            .executeDeleteFolderContent(
                deviceId = deviceId,
                folderPath = path,
            ).alsoSuccess {
                localFilesDataSource.storeFiles(
                    deviceId = deviceId,
                    parentPath = path,
                    files = it,
                )
                // store the result
            }.mapSuccess { }
    }

    override suspend fun deleteFile(
        deviceId: DeviceId,
        parentPath: FilePathDomainModel,
        path: FilePathDomainModel,
    ) = withContext(dispatcherProvider.data) {
        remoteFilesDataSource
            .executeDeleteFile(
                deviceId = deviceId,
                filePath = path,
                folderPath = parentPath,
            ).alsoSuccess {
                localFilesDataSource.storeFiles(
                    deviceId = deviceId,
                    parentPath = parentPath,
                    files = it,
                )
                // store the result
            }.mapSuccess { }
    }
}

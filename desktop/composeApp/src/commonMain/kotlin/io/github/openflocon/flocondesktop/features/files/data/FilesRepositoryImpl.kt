package io.github.openflocon.flocondesktop.features.files.data

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.FloconIncomingMessageDataModel
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocondesktop.common.Either
import io.github.openflocon.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import io.github.openflocon.flocondesktop.features.files.data.datasources.LocalFilesDataSource
import io.github.openflocon.flocondesktop.features.files.data.datasources.RemoteFilesDataSource
import io.github.openflocon.flocondesktop.features.files.data.mapper.decodeListFilesResult
import io.github.openflocon.flocondesktop.features.files.domain.model.FileDomainModel
import io.github.openflocon.flocondesktop.features.files.domain.model.FilePathDomainModel
import io.github.openflocon.flocondesktop.features.files.domain.repository.FilesRepository
import io.github.openflocon.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
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

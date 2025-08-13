package io.github.openflocon.flocondesktop.features.files.data

import com.flocon.data.remote.Protocol
import com.flocon.data.remote.files.mapper.toDomain
import com.flocon.data.remote.models.FloconIncomingMessageDataModel
import io.github.openflocon.data.core.files.datasource.FilesLocalDataSource
import io.github.openflocon.data.core.files.datasource.FilesRemoteDataSource
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.files.models.FileDomainModel
import io.github.openflocon.domain.files.models.FilePathDomainModel
import io.github.openflocon.domain.files.repository.FilesRepository
import io.github.openflocon.flocondesktop.features.files.data.mapper.decodeListFilesResult
import io.github.openflocon.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class FilesRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val localFilesDataSource: FilesLocalDataSource,
    private val remoteFilesDataSource: FilesRemoteDataSource,
) : FilesRepository,
    MessagesReceiverRepository {

    override val pluginName = listOf(Protocol.FromDevice.Files.Plugin)

    override suspend fun onNewDevice(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) {
        // no op
    }

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
                                received = received.toDomain(),
                            )
                        }
            }
        }
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
            .executeGetFile(
                deviceIdAndPackageName = deviceIdAndPackageName,
                path = path,
            ).alsoSuccess {
                localFilesDataSource.storeFiles(
                    deviceIdAndPackageName = deviceIdAndPackageName,
                    parentPath = path,
                    files = it,
                )
                // store the result
            }.mapSuccess { }
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
}

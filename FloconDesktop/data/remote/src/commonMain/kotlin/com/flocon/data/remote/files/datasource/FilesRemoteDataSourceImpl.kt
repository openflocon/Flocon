package com.flocon.data.remote.files.datasource

import co.touchlab.kermit.Logger
import com.flocon.data.remote.common.safeDecodeFromString
import com.flocon.data.remote.files.models.FromDeviceFilesResultDataModel
import com.flocon.data.remote.files.models.ToDeviceDeleteFileMessage
import com.flocon.data.remote.files.models.ToDeviceDeleteFolderContentMessage
import com.flocon.data.remote.files.models.ToDeviceGetFileMessage
import com.flocon.data.remote.files.models.ToDeviceGetFilesMessage
import com.flocon.data.remote.files.models.toDomain
import com.flocon.data.remote.models.FloconOutgoingMessageDataModel
import com.flocon.data.remote.models.toRemote
import com.flocon.data.remote.server.Server
import com.flocon.data.remote.server.newRequestId
import io.github.openflocon.data.core.files.datasource.FilesRemoteDataSource
import io.github.openflocon.domain.Protocol
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.files.models.FileDomainModel
import io.github.openflocon.domain.files.models.FilePathDomainModel
import io.github.openflocon.domain.files.models.FromDeviceFilesResultDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.messages.models.FloconReceivedFileDomainModel
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi

class FilesRemoteDataSourceImpl(
    private val server: Server,
    private val json: Json,
) : FilesRemoteDataSource {
    private val getFilesResultReceived =
        MutableStateFlow<Set<FromDeviceFilesResultDomainModel>>(emptySet())

    private val downloadFileResultReceived =
        MutableStateFlow<Set<FloconReceivedFileDomainModel>>(emptySet())

    override fun onGetFilesResultReceived(received: FromDeviceFilesResultDomainModel) {
        getFilesResultReceived.update { it + received }
    }

    override fun onFloconReceivedFilesDomainModel(received: FloconReceivedFileDomainModel) {
        downloadFileResultReceived.update { it + received }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun executeListFiles(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        path: FilePathDomainModel,
        withFoldersSize: Boolean,
    ): Either<Throwable, List<FileDomainModel>> {
        val requestId = newRequestId()

        val (filePath, isConstantPath) =
            when (path) {
                FilePathDomainModel.Constants.CachesDir -> "caches" to true
                FilePathDomainModel.Constants.FilesDir -> "files" to true
                is FilePathDomainModel.Real -> path.absolutePath to false
            }

        server.sendMessageToClient(
            deviceIdAndPackageName = deviceIdAndPackageName.toRemote(),
            message = FloconOutgoingMessageDataModel(
                plugin = Protocol.ToDevice.Files.Plugin,
                method = Protocol.ToDevice.Files.Method.ListFiles,
                body =
                Json.Default.encodeToString(
                    ToDeviceGetFilesMessage(
                        requestId = requestId,
                        path = filePath,
                        isConstantPath = isConstantPath,
                        withFoldersSize = withFoldersSize,
                    ),
                ),
            ),
        )
        // wait for result
        return waitForResult(requestId)
    }

    override suspend fun executeDownloadFile(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        path: String,
    ): Either<Throwable, FloconReceivedFileDomainModel> {
        val requestId = newRequestId() // not sure I need it
        server.sendMessageToClient(
            deviceIdAndPackageName = deviceIdAndPackageName.toRemote(),
            message = FloconOutgoingMessageDataModel(
                plugin = Protocol.ToDevice.Files.Plugin,
                method = Protocol.ToDevice.Files.Method.GetFile,
                body =
                Json.Default.encodeToString(
                    ToDeviceGetFileMessage(
                        requestId = requestId,
                        path = path,
                    ),
                ),
            ),
        )

        // wait for result
        return waitForDownloadFileResult(requestId)
    }

    override suspend fun executeDeleteFolderContent(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        folderPath: FilePathDomainModel,
    ): Either<Exception, List<FileDomainModel>> {
        val requestId = newRequestId()

        val (realPath, isConstantPath) =
            when (folderPath) {
                FilePathDomainModel.Constants.CachesDir -> "caches" to true
                FilePathDomainModel.Constants.FilesDir -> "files" to true
                is FilePathDomainModel.Real -> folderPath.absolutePath to false
            }

        server.sendMessageToClient(
            deviceIdAndPackageName = deviceIdAndPackageName.toRemote(),
            message = FloconOutgoingMessageDataModel(
                plugin = Protocol.ToDevice.Files.Plugin,
                method = Protocol.ToDevice.Files.Method.DeleteFolderContent,
                body =
                Json.Default.encodeToString(
                    ToDeviceDeleteFolderContentMessage(
                        requestId = requestId,
                        path = realPath,
                        isConstantPath = isConstantPath,
                    ),
                ),
            ),
        )

        // wait for result
        return waitForResult(requestId)
    }

    override suspend fun executeDeleteFile(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        folderPath: FilePathDomainModel,
        filePath: FilePathDomainModel,
    ): Either<Exception, List<FileDomainModel>> {
        val requestId = newRequestId()

        val (parentPath, isConstantParentPath) =
            when (folderPath) {
                FilePathDomainModel.Constants.CachesDir -> "caches" to true
                FilePathDomainModel.Constants.FilesDir -> "files" to true
                is FilePathDomainModel.Real -> folderPath.absolutePath to false
            }

        val filePathValue =
            when (filePath) {
                FilePathDomainModel.Constants.CachesDir -> return Failure(Exception("cannot delete cachedir"))
                FilePathDomainModel.Constants.FilesDir -> return Failure(Exception("cannot delete FilesDir"))
                is FilePathDomainModel.Real -> filePath.absolutePath
            }

        server.sendMessageToClient(
            deviceIdAndPackageName = deviceIdAndPackageName.toRemote(),
            message = FloconOutgoingMessageDataModel(
                plugin = Protocol.ToDevice.Files.Plugin,
                method = Protocol.ToDevice.Files.Method.DeleteFile,
                body =
                Json.Default.encodeToString(
                    ToDeviceDeleteFileMessage(
                        requestId = requestId,
                        parentPath = parentPath,
                        filePath = filePathValue,
                        isConstantParentPath = isConstantParentPath,
                    ),
                ),
            ),
        )

        // wait for result
        return waitForResult(requestId)
    }

    override fun getItems(message: FloconIncomingMessageDomainModel): FromDeviceFilesResultDomainModel? = json.safeDecodeFromString<FromDeviceFilesResultDataModel>(message.body)
        ?.toDomain()

    private suspend fun waitForResult(requestId: String): Either<Exception, List<FileDomainModel>> {
        try {
            val result = withTimeout(3_000) {
                getFilesResultReceived
                    .mapNotNull { it.firstOrNull { it.requestId == requestId } }
                    .first()
            }
            val files: List<FileDomainModel> = getFilesFromResult(result)
            return Success(files)
        } catch (e: TimeoutCancellationException) {
            Logger.e { "Timeout: No response for the file request $requestId" }
            return Failure(e)
        } catch (e: Exception) {
            Logger.e(e) { "Unknown exception: ${e.message}" }
            return Failure(e)
        }
    }

    private suspend fun waitForDownloadFileResult(requestId: String): Either<Exception, FloconReceivedFileDomainModel> {
        try {
            val result = withTimeout(30_000) {
                // 30s timeout
                downloadFileResultReceived
                    .mapNotNull { it.firstOrNull { it.requestId == requestId } }
                    .first()
            }
            return Success(result)
        } catch (e: TimeoutCancellationException) {
            Logger.e { "Timeout: No response for the file request $requestId" }
            return Failure(e)
        } catch (e: Exception) {
            Logger.e(e) { "Unknown exception: ${e.message}" }
            return Failure(e)
        }
    }

    private fun getFilesFromResult(result: FromDeviceFilesResultDomainModel): List<FileDomainModel> = result.files.map {
        FileDomainModel(
            name = it.name,
            isDirectory = it.isDirectory,
            path = FilePathDomainModel.Real(it.path),
            size = it.size,
            lastModified = Instant.fromEpochMilliseconds(it.lastModified),
        )
    }
}

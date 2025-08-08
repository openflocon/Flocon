package io.github.openflocon.flocondesktop.features.files.data.datasources

import io.github.openflocon.flocondesktop.FloconOutgoingMessageDataModel
import io.github.openflocon.flocondesktop.Protocol
import io.github.openflocon.flocondesktop.Server
import io.github.openflocon.flocondesktop.common.Either
import io.github.openflocon.flocondesktop.common.Failure
import io.github.openflocon.flocondesktop.common.Success
import io.github.openflocon.flocondesktop.features.files.data.model.incoming.FromDeviceFilesResultDataModel
import io.github.openflocon.flocondesktop.features.files.data.model.todevice.ToDeviceDeleteFileMessage
import io.github.openflocon.flocondesktop.features.files.data.model.todevice.ToDeviceDeleteFolderContentMessage
import io.github.openflocon.flocondesktop.features.files.data.model.todevice.ToDeviceGetFilesMessage
import io.github.openflocon.flocondesktop.features.files.domain.model.FileDomainModel
import io.github.openflocon.flocondesktop.features.files.domain.model.FilePathDomainModel
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.flocondesktop.messages.domain.model.toRemote
import io.github.openflocon.flocondesktop.newRequestId
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi

class RemoteFilesDataSource(
    private val server: Server,
) {
    private val getFilesResultReceived =
        MutableStateFlow<Set<FromDeviceFilesResultDataModel>>(emptySet())

    fun onGetFilesResultReceived(received: FromDeviceFilesResultDataModel) {
        getFilesResultReceived.update { it + received }
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun executeGetFile(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        path: FilePathDomainModel,
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
                    Json.encodeToString(
                        ToDeviceGetFilesMessage(
                            requestId = requestId,
                            path = filePath,
                            isConstantPath = isConstantPath,
                        ),
                    ),
            ),
        )
        // wait for result
        return waitForResult(requestId)
    }

    suspend fun executeDeleteFolderContent(
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
                    Json.encodeToString(
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

    suspend fun executeDeleteFile(
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
                    Json.encodeToString(
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

    private suspend fun waitForResult(requestId: String): Either<Exception, List<FileDomainModel>> {
        try {
            val result =
                withTimeout(3_000) {
                    getFilesResultReceived
                        .mapNotNull { it.firstOrNull { it.requestId == requestId } }
                        .first()
                }
            val files: List<FileDomainModel> = getFilesFromResult(result)
            return Success(files)
        } catch (e: TimeoutCancellationException) {
            // Ce bloc est exécuté si le timeout se produit
            println("Timeout: Aucune réponse reçue pour la requête $requestId dans le délai imparti.")
            // Tu peux ajouter ici d'autres logiques de gestion du timeout,
            // comme renvoyer une erreur à l'appelant, journaliser l'événement, etc.
            return Failure(e)
        } catch (e: Exception) {
            // Gère d'autres exceptions qui pourraient survenir
            println("Une erreur inattendue est survenue : ${e.message}")
            return Failure(e)
        }
    }

    private fun getFilesFromResult(result: FromDeviceFilesResultDataModel): List<FileDomainModel> = result.files.map {
        FileDomainModel(
            name = it.name,
            isDirectory = it.isDirectory,
            path = FilePathDomainModel.Real(it.path),
            size = it.size,
            lastModified = Instant.fromEpochMilliseconds(it.lastModified),
        )
    }
}

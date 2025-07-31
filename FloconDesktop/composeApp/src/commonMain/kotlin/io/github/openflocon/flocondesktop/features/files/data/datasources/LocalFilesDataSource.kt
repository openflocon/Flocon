package io.github.openflocon.flocondesktop.features.files.data.datasources

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.features.files.domain.model.FileDomainModel
import io.github.openflocon.flocondesktop.features.files.domain.model.FilePathDomainModel
import kotlinx.coroutines.flow.Flow

interface LocalFilesDataSource {
    fun observeFolderContentUseCase(
        deviceId: DeviceId,
        folderPath: FilePathDomainModel,
    ): Flow<List<FileDomainModel>>

    suspend fun storeFiles(
        deviceId: DeviceId,
        parentPath: FilePathDomainModel,
        files: List<FileDomainModel>,
    )
}

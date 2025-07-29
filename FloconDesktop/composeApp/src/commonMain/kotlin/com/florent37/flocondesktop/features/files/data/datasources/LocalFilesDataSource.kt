package com.florent37.flocondesktop.features.files.data.datasources

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.features.files.domain.model.FileDomainModel
import com.florent37.flocondesktop.features.files.domain.model.FilePathDomainModel
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

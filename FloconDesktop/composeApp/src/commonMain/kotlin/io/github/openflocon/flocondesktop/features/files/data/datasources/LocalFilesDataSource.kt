package io.github.openflocon.flocondesktop.features.files.data.datasources

import io.github.openflocon.flocondesktop.features.files.domain.model.FileDomainModel
import io.github.openflocon.flocondesktop.features.files.domain.model.FilePathDomainModel
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageName
import kotlinx.coroutines.flow.Flow

interface LocalFilesDataSource {
    fun observeFolderContentUseCase(
        deviceIdAndPackageName: DeviceIdAndPackageName,
        folderPath: FilePathDomainModel,
    ): Flow<List<FileDomainModel>>

    suspend fun storeFiles(
        deviceIdAndPackageName: DeviceIdAndPackageName,
        parentPath: FilePathDomainModel,
        files: List<FileDomainModel>,
    )
}

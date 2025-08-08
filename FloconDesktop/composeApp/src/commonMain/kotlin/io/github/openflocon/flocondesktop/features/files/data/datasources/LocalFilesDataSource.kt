package io.github.openflocon.flocondesktop.features.files.data.datasources

import io.github.openflocon.flocondesktop.features.files.domain.model.FileDomainModel
import io.github.openflocon.flocondesktop.features.files.domain.model.FilePathDomainModel
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface LocalFilesDataSource {
    fun observeFolderContentUseCase(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        folderPath: FilePathDomainModel,
    ): Flow<List<FileDomainModel>>

    suspend fun storeFiles(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        parentPath: FilePathDomainModel,
        files: List<FileDomainModel>,
    )
}

package io.github.openflocon.data.core.files.datasource

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.files.models.FileDomainModel
import io.github.openflocon.domain.files.models.FilePathDomainModel
import kotlinx.coroutines.flow.Flow

interface FilesLocalDataSource {
    fun observeFolderContentUseCase(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        folderPath: FilePathDomainModel,
    ): Flow<List<FileDomainModel>>

    suspend fun storeFiles(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        parentPath: FilePathDomainModel,
        files: List<FileDomainModel>,
    )

    fun observeWithFoldersSize(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<Boolean>

    suspend fun saveWithFoldersSize(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        value: Boolean
    )
}

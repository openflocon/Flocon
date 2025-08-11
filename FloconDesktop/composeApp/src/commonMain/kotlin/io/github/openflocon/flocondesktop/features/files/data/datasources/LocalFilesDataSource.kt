package io.github.openflocon.flocondesktop.features.files.data.datasources

import com.flocon.library.domain.models.FileDomainModel
import com.flocon.library.domain.models.FilePathDomainModel
import com.flocon.library.domain.models.DeviceIdAndPackageNameDomainModel
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

package io.github.openflocon.flocondesktop.features.files.data.datasources

import io.github.openflocon.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import io.github.openflocon.domain.models.FileDomainModel
import io.github.openflocon.domain.models.FilePathDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class LocalFilesDataSourceRoom(
    private val fileDao: FloconFileDao,
    private val dispatcherProvider: DispatcherProvider,
) : LocalFilesDataSource {

    override fun observeFolderContentUseCase(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        folderPath: FilePathDomainModel,
    ): Flow<List<FileDomainModel>> = fileDao
        .observeFolderContent(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
            parentFilePath = folderPath.mapToLocal(),
        ).map { list ->
            list.map { it.toDomainModel() }
        }.distinctUntilChanged()
        .flowOn(dispatcherProvider.data)

    override suspend fun storeFiles(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, parentPath: FilePathDomainModel, files: List<FileDomainModel>) {
        withContext(dispatcherProvider.data) {
            fileDao.clearFolderContent(
                deviceId = deviceIdAndPackageName.deviceId,
                packageName = deviceIdAndPackageName.packageName,
                parentPath = parentPath.mapToLocal(),
            )
            fileDao.insertFiles(
                files.map {
                    it.toEntity(
                        deviceId = deviceIdAndPackageName.deviceId,
                        packageName = deviceIdAndPackageName.packageName,
                        parentFilePath = parentPath,
                    )
                },
            )
        }
    }
}

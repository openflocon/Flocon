package io.github.openflocon.data.local.files.datasource

import io.github.openflocon.data.core.files.datasource.FilesLocalDataSource
import io.github.openflocon.data.local.files.dao.FloconFileDao
import io.github.openflocon.data.local.files.mapper.mapToLocal
import io.github.openflocon.data.local.files.mapper.toDomainModel
import io.github.openflocon.data.local.files.mapper.toEntity
import io.github.openflocon.data.local.files.models.FileOptionsEntity
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.files.models.FileDomainModel
import io.github.openflocon.domain.files.models.FilePathDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class LocalFilesDataSourceRoom(
    private val fileDao: FloconFileDao,
    private val dispatcherProvider: DispatcherProvider,
) : FilesLocalDataSource {

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

    override suspend fun storeFiles(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        parentPath: FilePathDomainModel,
        files: List<FileDomainModel>
    ) {
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

    override fun observeWithFoldersSize(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<Boolean> = fileDao
        .observeFileOptions(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName
        )
        .map { it?.withFoldersSize ?: false }
        .distinctUntilChanged()

    override suspend fun saveWithFoldersSize(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        value: Boolean
    ) {
        fileDao.updateFileOptions(
            option = FileOptionsEntity(
                deviceId = deviceIdAndPackageName.deviceId,
                packageName = deviceIdAndPackageName.packageName,
                withFoldersSize = value
            )
        )
    }
}

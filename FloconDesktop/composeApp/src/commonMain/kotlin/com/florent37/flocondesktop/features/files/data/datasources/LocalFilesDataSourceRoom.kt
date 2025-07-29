package com.florent37.flocondesktop.features.files.data.datasources

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import com.florent37.flocondesktop.features.files.domain.model.FileDomainModel
import com.florent37.flocondesktop.features.files.domain.model.FilePathDomainModel
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
        deviceId: DeviceId,
        folderPath: FilePathDomainModel,
    ): Flow<List<FileDomainModel>> = fileDao
        .observeFolderContent(
            deviceId = deviceId,
            parentFilePath = folderPath.mapToLocal(),
        ).map { list ->
            list.map { it.toDomainModel() }
        }.distinctUntilChanged()
        .flowOn(dispatcherProvider.data)

    override suspend fun storeFiles(
        deviceId: DeviceId,
        parentPath: FilePathDomainModel,
        files: List<FileDomainModel>,
    ) {
        withContext(dispatcherProvider.data) {
            fileDao.clearFolderContent(deviceId, parentPath.mapToLocal())
            fileDao.insertFiles(
                files.map {
                    it.toEntity(
                        deviceId = deviceId,
                        parentFilePath = parentPath,
                    )
                },
            )
        }
    }
}

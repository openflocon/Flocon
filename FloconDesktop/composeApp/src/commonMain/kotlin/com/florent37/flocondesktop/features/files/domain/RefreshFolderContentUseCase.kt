package com.florent37.flocondesktop.features.files.domain

import com.florent37.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import com.florent37.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import com.florent37.flocondesktop.features.files.domain.model.FilePathDomainModel
import com.florent37.flocondesktop.features.files.domain.repository.FilesRepository

class RefreshFolderContentUseCase(
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
    private val filesRepository: FilesRepository,
    private val dispatcherProvider: DispatcherProvider,
) {
    suspend operator fun invoke(
        path: FilePathDomainModel,
    ) {
        filesRepository.refreshFolderContent(
            deviceId = getCurrentDeviceIdUseCase() ?: return,
            path = path,
        )
    }
}

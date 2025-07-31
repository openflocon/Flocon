package io.github.openflocon.flocondesktop.features.files.domain

import io.github.openflocon.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import io.github.openflocon.flocondesktop.features.files.domain.model.FilePathDomainModel
import io.github.openflocon.flocondesktop.features.files.domain.repository.FilesRepository

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

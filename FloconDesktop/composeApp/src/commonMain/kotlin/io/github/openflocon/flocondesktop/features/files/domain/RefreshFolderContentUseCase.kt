package io.github.openflocon.flocondesktop.features.files.domain

import io.github.openflocon.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.models.FilePathDomainModel
import io.github.openflocon.flocondesktop.features.files.domain.repository.FilesRepository

class RefreshFolderContentUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val filesRepository: FilesRepository,
    private val dispatcherProvider: DispatcherProvider,
) {
    suspend operator fun invoke(
        path: FilePathDomainModel,
    ) {
        filesRepository.refreshFolderContent(
            deviceIdAndPackageName = getCurrentDeviceIdAndPackageNameUseCase() ?: return,
            path = path,
        )
    }
}

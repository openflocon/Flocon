package io.github.openflocon.domain.files.usecase

import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.files.models.FilePathDomainModel
import io.github.openflocon.domain.files.repository.FilesRepository

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

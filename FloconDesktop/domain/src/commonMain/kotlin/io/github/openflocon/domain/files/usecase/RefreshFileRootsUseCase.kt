package io.github.openflocon.domain.files.usecase

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.files.repository.FilesRepository

class RefreshFileRootsUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val filesRepository: FilesRepository,
) {
    suspend operator fun invoke() {
        filesRepository.refreshRoots(
            deviceIdAndPackageName = getCurrentDeviceIdAndPackageNameUseCase() ?: return,
        )
    }
}

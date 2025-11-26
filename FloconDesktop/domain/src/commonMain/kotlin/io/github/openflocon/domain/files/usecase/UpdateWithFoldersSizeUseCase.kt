package io.github.openflocon.domain.files.usecase

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.files.repository.FilesRepository

class UpdateWithFoldersSizeUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val filesRepository: FilesRepository,
) {
    suspend operator fun invoke(
        withFoldersSize: Boolean,
    ) {
        filesRepository.saveWithFoldersSize(
            deviceIdAndPackageName = getCurrentDeviceIdAndPackageNameUseCase() ?: return,
            value = withFoldersSize,
        )
    }
}


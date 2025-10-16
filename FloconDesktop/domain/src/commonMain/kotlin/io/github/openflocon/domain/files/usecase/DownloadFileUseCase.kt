package io.github.openflocon.domain.files.usecase

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.failure
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.files.models.FilePathDomainModel
import io.github.openflocon.domain.files.repository.FilesRepository

class DownloadFileUseCase(
    private val filesRepository: FilesRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
) {
    suspend operator fun invoke(
        path: FilePathDomainModel.Real,
    ) {
        val current = getCurrentDeviceIdAndPackageNameUseCase()
            ?: return

        return filesRepository.downloadFile(
            deviceIdAndPackageName = current,
            path = path.absolutePath,
        )
    }
}

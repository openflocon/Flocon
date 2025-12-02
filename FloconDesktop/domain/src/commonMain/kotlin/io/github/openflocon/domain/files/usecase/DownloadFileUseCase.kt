package io.github.openflocon.domain.files.usecase

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.files.models.FilePathDomainModel
import io.github.openflocon.domain.files.repository.FilesRepository
import io.github.openflocon.domain.messages.models.FloconReceivedFileDomainModel

class DownloadFileUseCase(
    private val filesRepository: FilesRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
) {
    suspend operator fun invoke(
        path: FilePathDomainModel.Real,
    ): Either<Throwable, FloconReceivedFileDomainModel> {
        val current = getCurrentDeviceIdAndPackageNameUseCase()
            ?: return Failure(Exception("No device connected"))

        return filesRepository.downloadFile(
            deviceIdAndPackageName = current,
            path = path.absolutePath,
        )
    }
}

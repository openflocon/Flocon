package io.github.openflocon.domain.files.usecase

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.failure
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.files.models.FilePathDomainModel
import io.github.openflocon.domain.files.repository.FilesRepository

class DeleteFilesUseCase(
    private val filesRepository: FilesRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
) {
    suspend operator fun invoke(
        paths: List<FilePathDomainModel>,
        parentPath: FilePathDomainModel,
    ): Either<Throwable, Unit> {
        val current = getCurrentDeviceIdAndPackageNameUseCase()
            ?: return Throwable("no device selected").failure()

        return filesRepository.deleteFiles(
            deviceIdAndPackageName = current,
            paths = paths,
            parentPath = parentPath,
        )
    }
}

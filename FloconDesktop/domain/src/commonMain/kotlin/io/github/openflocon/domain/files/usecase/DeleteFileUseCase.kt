package io.github.openflocon.domain.files.usecase

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.failure
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.files.models.FilePathDomainModel
import io.github.openflocon.domain.files.repository.FilesRepository

class DeleteFileUseCase(
    private val filesRepository: FilesRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val dispatcherProvider: DispatcherProvider,
) {
    suspend operator fun invoke(
        path: FilePathDomainModel,
        parentPath: FilePathDomainModel,
    ): Either<Throwable, Unit> {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return Throwable("no device selected").failure()

        return filesRepository.deleteFile(
            deviceIdAndPackageName = current,
            path = path,
            parentPath = parentPath,
        )
    }
}

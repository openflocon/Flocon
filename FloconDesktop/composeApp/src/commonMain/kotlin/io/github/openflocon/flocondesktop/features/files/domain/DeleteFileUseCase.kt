package io.github.openflocon.flocondesktop.features.files.domain

import com.florent37.flocondesktop.common.Either
import com.florent37.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import com.florent37.flocondesktop.common.failure
import com.florent37.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import com.florent37.flocondesktop.features.files.domain.model.FilePathDomainModel
import com.florent37.flocondesktop.features.files.domain.repository.FilesRepository

class DeleteFileUseCase(
    private val filesRepository: FilesRepository,
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
    private val dispatcherProvider: DispatcherProvider,
) {
    suspend operator fun invoke(
        path: FilePathDomainModel,
        parentPath: FilePathDomainModel,
    ): Either<Throwable, Unit> {
        val deviceId =
            getCurrentDeviceIdUseCase() ?: return Throwable("no device selected").failure()
        return filesRepository.deleteFile(
            deviceId = deviceId,
            path = path,
            parentPath = parentPath,
        )
    }
}

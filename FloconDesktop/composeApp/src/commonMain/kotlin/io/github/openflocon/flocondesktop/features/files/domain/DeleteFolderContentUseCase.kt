package io.github.openflocon.flocondesktop.features.files.domain

import io.github.openflocon.flocondesktop.common.Either
import io.github.openflocon.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import io.github.openflocon.flocondesktop.common.failure
import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import io.github.openflocon.flocondesktop.features.files.domain.model.FilePathDomainModel
import io.github.openflocon.flocondesktop.features.files.domain.repository.FilesRepository

class DeleteFolderContentUseCase(
    private val filesRepository: FilesRepository,
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
    private val dispatcherProvider: DispatcherProvider,
) {
    suspend operator fun invoke(
        path: FilePathDomainModel,
    ): Either<Throwable, Unit> {
        val deviceId = getCurrentDeviceIdUseCase() ?: return Throwable("no device selected").failure()
        return filesRepository.deleteFolderContent(
            deviceId = deviceId,
            path = path,
        )
    }
}

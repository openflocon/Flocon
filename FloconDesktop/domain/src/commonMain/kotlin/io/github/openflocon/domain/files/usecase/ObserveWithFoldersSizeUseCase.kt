package io.github.openflocon.domain.files.usecase

import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.files.repository.FilesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveWithFoldersSizeUseCase(
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    private val filesRepository: FilesRepository,
) {
    operator fun invoke(): Flow<Boolean> {
        return observeCurrentDeviceIdAndPackageNameUseCase().flatMapLatest { current ->
            if (current == null) {
                flowOf(false)
            } else {
                filesRepository.observeWithFoldersSize(current)
            }
        }
    }
}
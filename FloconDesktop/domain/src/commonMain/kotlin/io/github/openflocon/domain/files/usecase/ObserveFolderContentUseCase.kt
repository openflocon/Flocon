package io.github.openflocon.domain.files.usecase

import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.files.models.FileDomainModel
import io.github.openflocon.domain.files.models.FilePathDomainModel
import io.github.openflocon.domain.files.repository.FilesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ObserveFolderContentUseCase(
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    private val filesRepository: FilesRepository,
    private val dispatcherProvider: DispatcherProvider,
) {
    operator fun invoke(
        pathDomainModel: FilePathDomainModel,
        fetchScope: CoroutineScope,
    ): Flow<List<FileDomainModel>?> = observeCurrentDeviceIdAndPackageNameUseCase().flatMapLatest { current ->
        if (current == null) {
            flowOf(null)
        } else {
            filesRepository
                .observeFolderContent(
                    deviceIdAndPackageName = current,
                    path = pathDomainModel,
                ).onStart {
                    fetchScope.launch(dispatcherProvider.domain) {
                        filesRepository.refreshFolderContent(
                            deviceIdAndPackageName = current,
                            path = pathDomainModel,
                        )
                    }
                }
        }
    }
}

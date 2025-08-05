package io.github.openflocon.flocondesktop.features.files.domain

import io.github.openflocon.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import io.github.openflocon.flocondesktop.core.domain.device.ObserveCurrentDeviceIdUseCase
import io.github.openflocon.flocondesktop.features.files.domain.model.FileDomainModel
import io.github.openflocon.flocondesktop.features.files.domain.model.FilePathDomainModel
import io.github.openflocon.flocondesktop.features.files.domain.repository.FilesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ObserveFolderContentUseCase(
    private val observeCurrentDeviceIdUseCase: ObserveCurrentDeviceIdUseCase,
    private val filesRepository: FilesRepository,
    private val dispatcherProvider: DispatcherProvider,
) {
    operator fun invoke(
        pathDomainModel: FilePathDomainModel,
        fetchScope: CoroutineScope,
    ): Flow<List<FileDomainModel>?> = observeCurrentDeviceIdUseCase().flatMapLatest { deviceId ->
        if (deviceId == null) {
            flowOf(null)
        } else {
            filesRepository
                .observeFolderContent(
                    deviceId = deviceId,
                    path = pathDomainModel,
                ).onStart {
                    fetchScope.launch(dispatcherProvider.domain) {
                        filesRepository.refreshFolderContent(
                            deviceId = deviceId,
                            path = pathDomainModel,
                        )
                    }
                }
        }
    }
}

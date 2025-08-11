package io.github.openflocon.flocondesktop.features.images.domain

import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdUseCase
import io.github.openflocon.domain.models.DeviceImageDomainModel
import io.github.openflocon.flocondesktop.features.images.domain.repository.ImagesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveImagesUseCase(
    private val observeCurrentDeviceIdUseCase: ObserveCurrentDeviceIdUseCase,
    private val imagesRepository: ImagesRepository,
) {
    operator fun invoke(): Flow<List<DeviceImageDomainModel>> = observeCurrentDeviceIdUseCase().flatMapLatest { deviceId ->
        if (deviceId == null) flowOf(emptyList())
        else imagesRepository.observeImages(deviceId)
    }
}

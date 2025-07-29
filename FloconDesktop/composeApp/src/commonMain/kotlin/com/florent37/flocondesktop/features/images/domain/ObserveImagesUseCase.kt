package com.florent37.flocondesktop.features.images.domain

import com.florent37.flocondesktop.core.domain.device.ObserveCurrentDeviceIdUseCase
import com.florent37.flocondesktop.features.images.domain.model.DeviceImageDomainModel
import com.florent37.flocondesktop.features.images.domain.repository.ImagesRepository
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

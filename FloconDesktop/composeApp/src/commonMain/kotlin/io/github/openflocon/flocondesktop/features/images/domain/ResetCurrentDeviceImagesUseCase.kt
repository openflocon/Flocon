package io.github.openflocon.flocondesktop.features.images.domain

import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import io.github.openflocon.flocondesktop.features.images.domain.repository.ImagesRepository

class ResetCurrentDeviceImagesUseCase(
    private val imageRepository: ImagesRepository,
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
) {
    suspend operator fun invoke() {
        getCurrentDeviceIdUseCase()?.let {
            imageRepository.clearImages(deviceId = it)
        }
    }
}

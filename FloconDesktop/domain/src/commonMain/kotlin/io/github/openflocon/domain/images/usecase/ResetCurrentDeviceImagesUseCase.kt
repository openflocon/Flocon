package io.github.openflocon.domain.images.usecase

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdUseCase
import io.github.openflocon.domain.images.repository.ImagesRepository

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

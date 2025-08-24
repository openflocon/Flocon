package io.github.openflocon.domain.images.usecase

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.images.repository.ImagesRepository

class ResetCurrentDeviceImagesUseCase(
    private val imageRepository: ImagesRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
) {
    suspend operator fun invoke() {
        getCurrentDeviceIdAndPackageNameUseCase()?.let {
            imageRepository.clearImages(it)
        }
    }
}

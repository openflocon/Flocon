package io.github.openflocon.domain.deeplink.usecase

import io.github.openflocon.domain.deeplink.models.DeeplinkDomainModel
import io.github.openflocon.domain.deeplink.repository.DeeplinkRepository
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase

class AddToDeeplinkHistoryUseCase(
    private val deeplinkRepository: DeeplinkRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
) {
    suspend operator fun invoke(item: DeeplinkDomainModel) {
        getCurrentDeviceIdAndPackageNameUseCase()?.let {
            deeplinkRepository.addToHistory(item = item, deviceIdAndPackageName = it)
        }
    }
}

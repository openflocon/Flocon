package io.github.openflocon.domain.deeplink.usecase

import io.github.openflocon.domain.deeplink.models.DeeplinkDomainModel
import io.github.openflocon.domain.deeplink.repository.DeeplinkRepository
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase

class RemoveFromDeeplinkHistoryUseCase(
    private val deeplinkRepository: DeeplinkRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
) {
    suspend operator fun invoke(deeplinkId: Long) {
        getCurrentDeviceIdAndPackageNameUseCase()?.let {
            deeplinkRepository.removeFromHistory(deeplinkId = deeplinkId, deviceIdAndPackageName = it)
        }
    }
}

package io.github.openflocon.domain.featureflags.usecase

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.featureflags.repository.FeatureFlagsRepository

class SelectCurrentDeviceFeatureFlagProviderUseCase(
    private val featureFlagsRepository: FeatureFlagsRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
) {
    suspend operator fun invoke(provider: String) {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return

        featureFlagsRepository.selectProvider(
            deviceIdAndPackageName = current,
            provider = provider,
        )
    }
}

package io.github.openflocon.domain.featureflags.usecase

import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.featureflags.models.FeatureFlagDomainModel
import io.github.openflocon.domain.featureflags.repository.FeatureFlagsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveCurrentDeviceFeatureFlagUseCase(
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    private val featureFlagsRepository: FeatureFlagsRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(provider: String, name: String): Flow<FeatureFlagDomainModel?> = observeCurrentDeviceIdAndPackageNameUseCase()
        .flatMapLatest { deviceIdAndPackageName ->
            if (deviceIdAndPackageName == null) {
                flowOf(null)
            } else {
                featureFlagsRepository.observeFeatureFlag(
                    deviceIdAndPackageName = deviceIdAndPackageName,
                    provider = provider,
                    name = name,
                )
            }
        }
}

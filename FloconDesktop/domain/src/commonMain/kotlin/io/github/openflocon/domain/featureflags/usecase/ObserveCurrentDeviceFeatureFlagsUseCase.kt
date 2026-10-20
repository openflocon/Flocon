package io.github.openflocon.domain.featureflags.usecase

import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.featureflags.models.FeatureFlagDomainModel
import io.github.openflocon.domain.featureflags.repository.FeatureFlagsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

/**
 * The flags of the provider selected for the current device
 */
class ObserveCurrentDeviceFeatureFlagsUseCase(
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    private val featureFlagsRepository: FeatureFlagsRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(filter: String?): Flow<List<FeatureFlagDomainModel>> = observeCurrentDeviceIdAndPackageNameUseCase()
        .flatMapLatest { deviceIdAndPackageName ->
            if (deviceIdAndPackageName == null) {
                flowOf(emptyList())
            } else {
                featureFlagsRepository.observeSelectedProvider(deviceIdAndPackageName)
                    .flatMapLatest { provider ->
                        if (provider == null) {
                            flowOf(emptyList())
                        } else {
                            featureFlagsRepository.observeFeatureFlags(
                                deviceIdAndPackageName = deviceIdAndPackageName,
                                provider = provider,
                                filter = filter,
                            )
                        }
                    }
            }
        }
}

package io.github.openflocon.domain.featureflags

import io.github.openflocon.domain.featureflags.usecase.ObserveCurrentDeviceFeatureFlagProvidersUseCase
import io.github.openflocon.domain.featureflags.usecase.ObserveCurrentDeviceFeatureFlagUseCase
import io.github.openflocon.domain.featureflags.usecase.ObserveCurrentDeviceFeatureFlagsUseCase
import io.github.openflocon.domain.featureflags.usecase.ObserveCurrentDeviceSelectedFeatureFlagProviderUseCase
import io.github.openflocon.domain.featureflags.usecase.SelectCurrentDeviceFeatureFlagProviderUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val featureFlagsModule = module {
    factoryOf(::ObserveCurrentDeviceFeatureFlagsUseCase)
    factoryOf(::ObserveCurrentDeviceFeatureFlagUseCase)
    factoryOf(::ObserveCurrentDeviceFeatureFlagProvidersUseCase)
    factoryOf(::ObserveCurrentDeviceSelectedFeatureFlagProviderUseCase)
    factoryOf(::SelectCurrentDeviceFeatureFlagProviderUseCase)
}

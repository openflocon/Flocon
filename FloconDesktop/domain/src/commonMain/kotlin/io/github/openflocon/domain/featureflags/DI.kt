package io.github.openflocon.domain.featureflags

import io.github.openflocon.domain.featureflags.usecase.ObserveCurrentDeviceFeatureFlagUseCase
import io.github.openflocon.domain.featureflags.usecase.ObserveCurrentDeviceFeatureFlagsUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val featureFlagsModule = module {
    factoryOf(::ObserveCurrentDeviceFeatureFlagsUseCase)
    factoryOf(::ObserveCurrentDeviceFeatureFlagUseCase)
}

package io.github.openflocon.flocondesktop.features.featureflags

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featureFlagsModule = module {
    viewModelOf(::FeatureFlagsViewModel)
    viewModelOf(::FeatureFlagDetailViewModel)
}

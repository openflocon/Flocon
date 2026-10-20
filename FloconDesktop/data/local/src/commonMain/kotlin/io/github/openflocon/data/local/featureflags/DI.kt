package io.github.openflocon.data.local.featureflags

import io.github.openflocon.data.core.featureflags.datasource.FeatureFlagsLocalDataSource
import io.github.openflocon.data.core.featureflags.datasource.SelectedFeatureFlagProviderDataSource
import io.github.openflocon.data.local.featureflags.datasource.FeatureFlagsLocalDataSourceRoom
import io.github.openflocon.data.local.featureflags.datasource.SelectedFeatureFlagProviderDataSourceInMemory
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val featureFlagsLocalModule = module {
    singleOf(::FeatureFlagsLocalDataSourceRoom) bind FeatureFlagsLocalDataSource::class
    singleOf(::SelectedFeatureFlagProviderDataSourceInMemory) bind SelectedFeatureFlagProviderDataSource::class
}

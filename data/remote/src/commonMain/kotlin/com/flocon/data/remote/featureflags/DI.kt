package com.flocon.data.remote.featureflags

import com.flocon.data.remote.featureflags.datasource.FeatureFlagsRemoteDataSourceImpl
import io.github.openflocon.data.core.featureflags.datasource.FeatureFlagsRemoteDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val featureFlagsModule = module {
    singleOf(::FeatureFlagsRemoteDataSourceImpl) bind FeatureFlagsRemoteDataSource::class
}

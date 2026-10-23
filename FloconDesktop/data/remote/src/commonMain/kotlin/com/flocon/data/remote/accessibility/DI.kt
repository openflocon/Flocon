package com.flocon.data.remote.accessibility

import com.flocon.data.remote.accessibility.datasource.AccessibilityRemoteDataSourceImpl
import io.github.openflocon.data.core.accessibility.datasource.AccessibilityRemoteDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val accessibilityModule = module {
    singleOf(::AccessibilityRemoteDataSourceImpl) bind AccessibilityRemoteDataSource::class
}

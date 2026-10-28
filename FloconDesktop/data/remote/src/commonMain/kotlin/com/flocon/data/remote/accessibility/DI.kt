package com.flocon.data.remote.accessibility

import com.flocon.data.remote.accessibility.datasource.AccessibilityRemoteDataSourceImpl
import com.flocon.data.remote.accessibility.datasource.ToDeviceAccessibilityDataSourceImpl
import io.github.openflocon.data.core.accessibility.datasource.AccessibilityRemoteDataSource
import io.github.openflocon.data.core.accessibility.datasource.ToDeviceAccessibilityDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val accessibilityModule = module {
    singleOf(::AccessibilityRemoteDataSourceImpl) bind AccessibilityRemoteDataSource::class
    singleOf(::ToDeviceAccessibilityDataSourceImpl) bind ToDeviceAccessibilityDataSource::class
}

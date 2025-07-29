package com.florent37.flocondesktop.features.analytics.domain.di

import com.florent37.flocondesktop.features.analytics.domain.GetCurrentDeviceSelectedAnalyticsUseCase
import com.florent37.flocondesktop.features.analytics.domain.ObserveCurrentDeviceAnalyticsContentUseCase
import com.florent37.flocondesktop.features.analytics.domain.ObserveCurrentDeviceSelectedAnalyticsUseCase
import com.florent37.flocondesktop.features.analytics.domain.ObserveDeviceAnalyticsUseCase
import com.florent37.flocondesktop.features.analytics.domain.ResetCurrentDeviceSelectedAnalyticsUseCase
import com.florent37.flocondesktop.features.analytics.domain.SelectCurrentDeviceAnalyticsUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val analyticsDomainModule = module {
    factoryOf(::GetCurrentDeviceSelectedAnalyticsUseCase)
    factoryOf(::ObserveCurrentDeviceSelectedAnalyticsUseCase)
    factoryOf(::ObserveDeviceAnalyticsUseCase)
    factoryOf(::SelectCurrentDeviceAnalyticsUseCase)
    factoryOf(::ObserveCurrentDeviceAnalyticsContentUseCase)
    factoryOf(::ResetCurrentDeviceSelectedAnalyticsUseCase)
}

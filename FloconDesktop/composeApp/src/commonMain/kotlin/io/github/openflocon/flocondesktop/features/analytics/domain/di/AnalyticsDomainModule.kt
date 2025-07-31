package io.github.openflocon.flocondesktop.features.analytics.domain.di

import io.github.openflocon.flocondesktop.features.analytics.domain.GetCurrentDeviceSelectedAnalyticsUseCase
import io.github.openflocon.flocondesktop.features.analytics.domain.ObserveCurrentDeviceAnalyticsContentUseCase
import io.github.openflocon.flocondesktop.features.analytics.domain.ObserveCurrentDeviceSelectedAnalyticsUseCase
import io.github.openflocon.flocondesktop.features.analytics.domain.ObserveDeviceAnalyticsUseCase
import io.github.openflocon.flocondesktop.features.analytics.domain.ResetCurrentDeviceSelectedAnalyticsUseCase
import io.github.openflocon.flocondesktop.features.analytics.domain.SelectCurrentDeviceAnalyticsUseCase
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

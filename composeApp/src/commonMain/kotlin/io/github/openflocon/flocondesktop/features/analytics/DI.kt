package io.github.openflocon.flocondesktop.features.analytics

import io.github.openflocon.flocondesktop.features.analytics.delegate.AnalyticsSelectorDelegate
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val analyticsModule = module {
    viewModelOf(::AnalyticsViewModel)
    viewModelOf(::AnalyticsDetailViewModel)
    factoryOf(::AnalyticsSelectorDelegate)
}

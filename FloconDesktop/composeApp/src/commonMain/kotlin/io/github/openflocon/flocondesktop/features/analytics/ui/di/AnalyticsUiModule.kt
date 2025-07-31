package io.github.openflocon.flocondesktop.features.analytics.ui.di

import com.florent37.flocondesktop.features.analytics.ui.AnalyticsViewModel
import com.florent37.flocondesktop.features.analytics.ui.delegate.AnalyticsSelectorDelegate
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val analyticsUiModule =
    module {
        viewModelOf(::AnalyticsViewModel)
        factoryOf(::AnalyticsSelectorDelegate)
    }

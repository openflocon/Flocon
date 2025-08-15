package io.github.openflocon.flocondesktop.features.dashboard

import io.github.openflocon.flocondesktop.features.dashboard.delegate.DashboardSelectorDelegate
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val dashboardModule = module {
    viewModelOf(::DashboardViewModel)
    factoryOf(::DashboardSelectorDelegate)
}

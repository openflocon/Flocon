package io.github.openflocon.flocondesktop.features.dashboard.ui.di

import com.florent37.flocondesktop.features.dashboard.ui.DashboardViewModel
import com.florent37.flocondesktop.features.dashboard.ui.delegate.DashboardSelectorDelegate
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val dashboardUiModule =
    module {
        viewModelOf(::DashboardViewModel)
        factoryOf(::DashboardSelectorDelegate)
    }

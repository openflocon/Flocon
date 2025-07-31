package io.github.openflocon.flocondesktop.features.dashboard.di

import com.florent37.flocondesktop.features.dashboard.data.di.dashboardDataModule
import com.florent37.flocondesktop.features.dashboard.domain.di.dashboardDomainModule
import com.florent37.flocondesktop.features.dashboard.ui.di.dashboardUiModule
import org.koin.dsl.module

val dashboardModule =
    module {
        includes(
            dashboardDataModule,
            dashboardDomainModule,
            dashboardUiModule,
        )
    }

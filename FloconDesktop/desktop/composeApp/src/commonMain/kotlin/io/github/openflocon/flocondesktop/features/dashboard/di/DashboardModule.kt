package io.github.openflocon.flocondesktop.features.dashboard.di

import io.github.openflocon.flocondesktop.features.dashboard.data.di.dashboardDataModule
import io.github.openflocon.flocondesktop.features.dashboard.domain.di.dashboardDomainModule
import io.github.openflocon.flocondesktop.features.dashboard.ui.di.dashboardUiModule
import org.koin.dsl.module

val dashboardModule =
    module {
        includes(
            dashboardDataModule,
            dashboardDomainModule,
            dashboardUiModule,
        )
    }

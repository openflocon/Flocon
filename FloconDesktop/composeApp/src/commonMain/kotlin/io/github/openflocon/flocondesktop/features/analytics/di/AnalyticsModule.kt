package io.github.openflocon.flocondesktop.features.analytics.di

import io.github.openflocon.flocondesktop.features.analytics.data.di.analyticsDataModule
import io.github.openflocon.flocondesktop.features.analytics.ui.di.analyticsUiModule
import org.koin.dsl.module

val analyticsModule = module {
    includes(
        analyticsDataModule,
        analyticsUiModule,
    )
}

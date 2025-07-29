package com.florent37.flocondesktop.features.analytics.di

import com.florent37.flocondesktop.features.analytics.data.di.analyticsDataModule
import com.florent37.flocondesktop.features.analytics.domain.di.analyticsDomainModule
import com.florent37.flocondesktop.features.analytics.ui.di.analyticsUiModule
import org.koin.dsl.module

val analyticsModule =
    module {
        includes(
            analyticsDataModule,
            analyticsDomainModule,
            analyticsUiModule,
        )
    }

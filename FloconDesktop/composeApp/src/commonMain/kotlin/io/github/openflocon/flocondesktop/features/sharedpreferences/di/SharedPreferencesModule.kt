package io.github.openflocon.flocondesktop.features.sharedpreferences.di

import io.github.openflocon.flocondesktop.features.sharedpreferences.data.di.sharedPreferencesDataModule
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.di.sharedPreferencesDomainModule
import io.github.openflocon.flocondesktop.features.sharedpreferences.ui.di.sharedPreferencesUiModule
import org.koin.dsl.module

val sharedPreferencesModule =
    module {
        includes(
            sharedPreferencesDataModule,
            sharedPreferencesDomainModule,
            sharedPreferencesUiModule,
        )
    }

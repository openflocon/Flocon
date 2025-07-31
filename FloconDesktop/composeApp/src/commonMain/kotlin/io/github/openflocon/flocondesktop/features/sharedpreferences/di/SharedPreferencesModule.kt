package io.github.openflocon.flocondesktop.features.sharedpreferences.di

import com.florent37.flocondesktop.features.sharedpreferences.data.di.sharedPreferencesDataModule
import com.florent37.flocondesktop.features.sharedpreferences.domain.di.sharedPreferencesDomainModule
import com.florent37.flocondesktop.features.sharedpreferences.ui.di.sharedPreferencesUiModule
import org.koin.dsl.module

val sharedPreferencesModule =
    module {
        includes(
            sharedPreferencesDataModule,
            sharedPreferencesDomainModule,
            sharedPreferencesUiModule,
        )
    }

package io.github.openflocon.flocondesktop.features.sharedpreferences.ui.di

import io.github.openflocon.flocondesktop.features.sharedpreferences.ui.SharedPreferencesViewModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.ui.delegate.SharedPrefSelectorDelegate
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val sharedPreferencesUiModule =
    module {
        viewModelOf(::SharedPreferencesViewModel)
        factoryOf(::SharedPrefSelectorDelegate)
    }

package io.github.openflocon.flocondesktop.features.sharedpreferences

import io.github.openflocon.flocondesktop.features.sharedpreferences.delegate.SharedPrefSelectorDelegate
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val sharedPreferencesModule = module {
    viewModelOf(::SharedPreferencesViewModel)
    factoryOf(::SharedPrefSelectorDelegate)
}

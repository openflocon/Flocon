package io.github.openflocon.flocondesktop.features.database.ui.di

import com.florent37.flocondesktop.features.database.ui.DatabaseViewModel
import com.florent37.flocondesktop.features.database.ui.delegate.DatabaseSelectorDelegate
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val databaseUiModule =
    module {
        viewModelOf(::DatabaseViewModel)
        factoryOf(::DatabaseSelectorDelegate)
    }

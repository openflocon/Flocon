package io.github.openflocon.flocondesktop.features.database

import io.github.openflocon.flocondesktop.features.database.delegate.DatabaseSelectorDelegate
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val databaseModule = module {
    viewModelOf(::DatabaseViewModel)
    viewModelOf(::DatabaseTabViewModel)
    factoryOf(::DatabaseSelectorDelegate)
}

package io.github.openflocon.flocondesktop.features.database

import io.github.openflocon.flocondesktop.features.database.delegate.DatabaseSelectorDelegate
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val databaseModule = module {
    viewModelOf(::DatabaseViewModel)
    singleOf(::DatabaseSelectorDelegate)
}

package io.github.openflocon.flocondesktop.features.database

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val databaseModule = module {
    viewModelOf(::DatabaseViewModel)
}

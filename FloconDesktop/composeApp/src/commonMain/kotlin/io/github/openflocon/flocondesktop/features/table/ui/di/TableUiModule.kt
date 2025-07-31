package io.github.openflocon.flocondesktop.features.table.ui.di

import io.github.openflocon.flocondesktop.features.table.ui.TableViewModel
import io.github.openflocon.flocondesktop.features.table.ui.delegate.TableSelectorDelegate
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val tableUiModule =
    module {
        viewModelOf(::TableViewModel)
        factoryOf(::TableSelectorDelegate)
    }

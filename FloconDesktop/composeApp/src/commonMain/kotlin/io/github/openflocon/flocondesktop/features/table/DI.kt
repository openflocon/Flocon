package io.github.openflocon.flocondesktop.features.table

import io.github.openflocon.flocondesktop.features.table.delegate.TableSelectorDelegate
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val tableModule = module {
    viewModelOf(::TableViewModel)
    factoryOf(::TableSelectorDelegate)
}

package io.github.openflocon.flocondesktop.features.deeplinks

import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val deeplinkModule = module {
    viewModelOf(::DeepLinkViewModel)
    viewModel { (id: Long) -> DeeplinkPanelViewModel(id, get(), get()) }
}

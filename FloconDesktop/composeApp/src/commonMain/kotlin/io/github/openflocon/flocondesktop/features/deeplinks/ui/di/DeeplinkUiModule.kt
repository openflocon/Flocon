package io.github.openflocon.flocondesktop.features.deeplinks.ui.di

import io.github.openflocon.flocondesktop.features.deeplinks.ui.DeepLinkViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val deeplinkUiModule =
    module {
        viewModelOf(::DeepLinkViewModel)
    }

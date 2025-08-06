package io.github.openflocon.flocondesktop.features.network.ui.di

import io.github.openflocon.flocondesktop.features.network.ui.HeaderDelegate
import io.github.openflocon.flocondesktop.features.network.ui.NetworkViewModel
import io.github.openflocon.flocondesktop.messages.ui.MessagesServerDelegate
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val networkUiModule =
    module {
        viewModelOf(::NetworkViewModel)
        factoryOf(::MessagesServerDelegate)
        factoryOf(::HeaderDelegate)
    }

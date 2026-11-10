package io.github.openflocon.flocondesktop.features.connect

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val connectModule = module {
    viewModelOf(::ConnectViewModel)
}

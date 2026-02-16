package io.github.openflocon.flocondesktop.features.command

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val adbCommandModule = module {
    viewModelOf(::AdbCommandViewModel)
}

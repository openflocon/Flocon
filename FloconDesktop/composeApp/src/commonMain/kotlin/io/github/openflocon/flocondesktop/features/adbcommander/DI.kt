package io.github.openflocon.flocondesktop.features.adbcommander

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val adbCommanderModule = module {
    viewModelOf(::AdbCommanderViewModel)
}

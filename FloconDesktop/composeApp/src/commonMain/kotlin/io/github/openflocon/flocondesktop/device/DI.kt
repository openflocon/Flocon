package io.github.openflocon.flocondesktop.device

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val deviceModule = module {
    viewModelOf(::DeviceViewModel)
}

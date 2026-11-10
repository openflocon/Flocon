package io.github.openflocon.flocondesktop.features.adbcommander

import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val adbCommanderModule = module {
    viewModelOf(::AdbCommanderViewModel)
    viewModel { (id: Long) -> AdbCommandPanelViewModel(id, get(), get()) }
}

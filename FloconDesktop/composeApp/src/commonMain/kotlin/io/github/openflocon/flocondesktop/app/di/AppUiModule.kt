package io.github.openflocon.flocondesktop.app.di

import io.github.openflocon.flocondesktop.app.AppViewModel
import io.github.openflocon.flocondesktop.app.ui.delegates.DevicesDelegate
import io.github.openflocon.flocondesktop.app.ui.delegates.RecordVideoDelegate
import io.github.openflocon.flocondesktop.app.version.VersionCheckerViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appUiModule = module {
    viewModelOf(::AppViewModel)
    factoryOf(::DevicesDelegate)
    factoryOf(::RecordVideoDelegate)
    viewModelOf(::VersionCheckerViewModel)
}

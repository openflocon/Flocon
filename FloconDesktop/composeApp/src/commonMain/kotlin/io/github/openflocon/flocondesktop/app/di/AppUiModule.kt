package io.github.openflocon.flocondesktop.app.di

import io.github.openflocon.flocondesktop.app.AppViewModel
import io.github.openflocon.flocondesktop.app.TimelineScreenshotsRunner
import io.github.openflocon.flocondesktop.app.ui.delegates.DevicesDelegate
import io.github.openflocon.flocondesktop.app.ui.delegates.RecordVideoDelegate
import io.github.openflocon.flocondesktop.app.ui.mascot.MascotViewModel
import io.github.openflocon.flocondesktop.app.version.VersionCheckerViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appUiModule = module {
    factoryOf(::TimelineScreenshotsRunner)
    // 23 dependencies: above the 22 supported by viewModelOf
    viewModel {
        AppViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    }
    factoryOf(::DevicesDelegate)
    factoryOf(::RecordVideoDelegate)
    viewModelOf(::VersionCheckerViewModel)
    viewModelOf(::MascotViewModel)
}

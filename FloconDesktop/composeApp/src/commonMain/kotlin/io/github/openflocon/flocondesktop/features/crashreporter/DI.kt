package io.github.openflocon.flocondesktop.features.crashreporter

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val crashReporterModule = module {
    viewModelOf(::CrashReporterViewModel)
}

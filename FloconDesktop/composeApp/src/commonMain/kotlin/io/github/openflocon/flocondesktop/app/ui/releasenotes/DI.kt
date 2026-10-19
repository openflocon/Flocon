package io.github.openflocon.flocondesktop.app.ui.releasenotes

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val releaseNotesModule = module {
    viewModelOf(::ReleaseNotesViewModel)
}

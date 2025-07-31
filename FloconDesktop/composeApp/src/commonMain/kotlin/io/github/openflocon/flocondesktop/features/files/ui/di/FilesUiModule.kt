package io.github.openflocon.flocondesktop.features.files.ui.di

import io.github.openflocon.flocondesktop.features.files.ui.FilesViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val filesUiModule =
    module {
        viewModelOf(::FilesViewModel)
    }

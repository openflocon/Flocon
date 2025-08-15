package io.github.openflocon.flocondesktop.features.files

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val filesModule = module {
    viewModelOf(::FilesViewModel)
}

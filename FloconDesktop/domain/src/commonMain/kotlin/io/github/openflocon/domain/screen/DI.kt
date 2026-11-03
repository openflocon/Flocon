package io.github.openflocon.domain.screen

import org.koin.dsl.module

internal val screenModule = module {
    single { ScreenRequestStore() }
}

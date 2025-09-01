package io.github.openflocon.navigation

import org.koin.dsl.module

val navigationModule = module {
    // Add qualifier ?
    single { FloconNavigationState() }
}

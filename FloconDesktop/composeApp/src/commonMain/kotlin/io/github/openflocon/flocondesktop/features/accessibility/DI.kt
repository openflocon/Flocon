package io.github.openflocon.flocondesktop.features.accessibility

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val accessibilityModule = module {
    viewModelOf(::AccessibilityViewModel)
    viewModelOf(::AccessibilityDetailViewModel)
}

package io.github.openflocon.flocondesktop.features.timeline

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val timelineModule = module {
    viewModelOf(::TimelineViewModel)
}

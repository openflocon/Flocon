package io.github.openflocon.flocondesktop.features.timeline

import io.github.openflocon.navigation.scene.PanelPinRegistry
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val timelineModule = module {
    viewModelOf(::TimelineViewModel)
    singleOf(::TimelinePanelPin) {
        bind<PanelPinRegistry>()
    }
}

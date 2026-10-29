package io.github.openflocon.flocondesktop.features.timeline

import io.github.openflocon.navigation.scene.PanelPinRegistry
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val timelineModule = module {
    singleOf(::TimelineFilterStore)
    viewModelOf(::TimelineViewModel)
    viewModelOf(::TimelineFilterPanelViewModel)
    singleOf(::TimelinePanelPin) {
        bind<PanelPinRegistry>()
    }
}

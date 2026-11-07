package io.github.openflocon.flocondesktop.features.timeline

import io.github.openflocon.domain.timeline.repository.TimelineUserFilterProvider
import io.github.openflocon.navigation.scene.PanelPinRegistry
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val timelineModule = module {
    singleOf(::TimelineFilterStore) {
        bind<TimelineUserFilterProvider>()
    }
    // more parameters than viewModelOf supports
    viewModel { TimelineViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModelOf(::TimelineFilterPanelViewModel)
    viewModel { (id: Long) -> TimelineLogDetailViewModel(id, get(), get()) }
    singleOf(::TimelinePanelPin) {
        bind<PanelPinRegistry>()
    }
}

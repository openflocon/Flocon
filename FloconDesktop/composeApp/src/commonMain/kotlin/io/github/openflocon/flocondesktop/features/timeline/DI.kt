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
    // viewModelOf only supports up to 22 constructor parameters, TimelineViewModel has more
    viewModel {
        TimelineViewModel(
            observeTimelineUseCase = get(),
            observeTimelineSummaryUseCase = get(),
            observeTimelineEventsUseCase = get(),
            observeIsCurrentDeviceAndAppActiveUseCase = get(),
            observeTimelineSettingsUseCase = get(),
            observeTimelineScreenshotsFailingUseCase = get(),
            observeTimelineScreenshotsSupportedUseCase = get(),
            observeTimelineScreenshotsUseCase = get(),
            setTimelineAutoScrollUseCase = get(),
            setTimelineInvertListUseCase = get(),
            setTimelinePinPanelUseCase = get(),
            setTimelineGraphUseCase = get(),
            setTimelineMergePluginsUseCase = get(),
            setTimelineClusterEventsUseCase = get(),
            setTimelineScreenshotsUseCase = get(),
            setTimelineScreenshotIntervalUseCase = get(),
            setTimelineSelectedSourcesUseCase = get(),
            filterStore = get(),
            dispatcherProvider = get(),
            navigationState = get(),
            networkItemOptionsDelegate = get(),
            observeTimelineFocusUseCase = get(),
            observeTransactionsExpandedByDefaultUseCase = get(),
            setTransactionsExpandedByDefaultUseCase = get(),
            consumeTimelineFocusUseCase = get(),
            observeCurrentDeviceIdAndPackageNameUseCase = get(),
            observeCurrentDeviceAndAppUseCase = get(),
        )
    }
    viewModelOf(::TimelineFilterPanelViewModel)
    viewModel { (id: Long) -> TimelineLogDetailViewModel(id, get(), get()) }
    singleOf(::TimelinePanelPin) {
        bind<PanelPinRegistry>()
    }
}

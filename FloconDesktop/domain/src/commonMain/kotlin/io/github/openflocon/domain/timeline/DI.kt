package io.github.openflocon.domain.timeline

import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.device.usecase.ObserveIsCurrentDeviceIAndAppIsActiveUseCase
import io.github.openflocon.domain.timeline.screenshot.TimelineScreenCapturer
import io.github.openflocon.domain.timeline.screenshot.TimelineScreenCapturerImpl
import io.github.openflocon.domain.timeline.screenshot.TimelineScreenshotRecorder
import io.github.openflocon.domain.timeline.usecase.ConsumeTimelineFocusUseCase
import io.github.openflocon.domain.timeline.usecase.GetTimelineEventHiddenReasonsUseCase
import io.github.openflocon.domain.timeline.usecase.GetTimelineEventsUseCase
import io.github.openflocon.domain.timeline.usecase.GetTimelineSummaryUseCase
import io.github.openflocon.domain.timeline.usecase.ObserveTimelineEventsUseCase
import io.github.openflocon.domain.timeline.usecase.ObserveTimelineFocusUseCase
import io.github.openflocon.domain.timeline.usecase.ObserveTimelineScreenshotsFailingUseCase
import io.github.openflocon.domain.timeline.usecase.ObserveTimelineScreenshotsSupportedUseCase
import io.github.openflocon.domain.timeline.usecase.ObserveTimelineScreenshotsUseCase
import io.github.openflocon.domain.timeline.usecase.ObserveTimelineSettingsUseCase
import io.github.openflocon.domain.timeline.usecase.ObserveTimelineSummaryUseCase
import io.github.openflocon.domain.timeline.usecase.ObserveTimelineUseCase
import io.github.openflocon.domain.timeline.usecase.PurgeTimelineScreenshotsUseCase
import io.github.openflocon.domain.timeline.usecase.RequestTimelineFocusUseCase
import io.github.openflocon.domain.timeline.usecase.SetTimelineAutoScrollUseCase
import io.github.openflocon.domain.timeline.usecase.SetTimelineClusterEventsUseCase
import io.github.openflocon.domain.timeline.usecase.SetTimelineGraphUseCase
import io.github.openflocon.domain.timeline.usecase.SetTimelineInvertListUseCase
import io.github.openflocon.domain.timeline.usecase.SetTimelineMergePluginsUseCase
import io.github.openflocon.domain.timeline.usecase.SetTimelinePinPanelUseCase
import io.github.openflocon.domain.timeline.usecase.SetTimelineScreenshotIntervalUseCase
import io.github.openflocon.domain.timeline.usecase.SetTimelineScreenshotsUseCase
import io.github.openflocon.domain.timeline.usecase.SetTimelineSelectedSourcesUseCase
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val timelineDomainModule = module {
    factoryOf(::ObserveTimelineUseCase)
    factoryOf(::ObserveTimelineEventsUseCase)
    factoryOf(::GetTimelineEventsUseCase)
    factoryOf(::GetTimelineSummaryUseCase)
    factoryOf(::RequestTimelineFocusUseCase)
    factoryOf(::GetTimelineEventHiddenReasonsUseCase)
    factoryOf(::ObserveTimelineFocusUseCase)
    factoryOf(::ConsumeTimelineFocusUseCase)
    factoryOf(::ObserveTimelineSummaryUseCase)
    factoryOf(::ObserveTimelineSettingsUseCase)
    factoryOf(::SetTimelineAutoScrollUseCase)
    factoryOf(::SetTimelineInvertListUseCase)
    factoryOf(::SetTimelinePinPanelUseCase)
    factoryOf(::SetTimelineSelectedSourcesUseCase)
    factoryOf(::SetTimelineGraphUseCase)
    factoryOf(::SetTimelineMergePluginsUseCase)
    factoryOf(::SetTimelineClusterEventsUseCase)
    factoryOf(::SetTimelineScreenshotsUseCase)
    factoryOf(::SetTimelineScreenshotIntervalUseCase)
    factoryOf(::ObserveTimelineScreenshotsUseCase)
    factoryOf(::ObserveTimelineScreenshotsFailingUseCase)
    factoryOf(::ObserveTimelineScreenshotsSupportedUseCase)
    factoryOf(::PurgeTimelineScreenshotsUseCase)
    singleOf(::TimelineScreenCapturerImpl) { bind<TimelineScreenCapturer>() }
    single {
        TimelineScreenshotRecorder(
            timelinePreferencesRepository = get(),
            currentDevice = get<ObserveCurrentDeviceIdAndPackageNameUseCase>().invoke(),
            isCurrentDeviceLive = get<ObserveIsCurrentDeviceIAndAppIsActiveUseCase>().invoke(),
            capturer = get(),
            repository = get(),
        )
    }
}

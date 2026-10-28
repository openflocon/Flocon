package io.github.openflocon.domain.timeline

import io.github.openflocon.domain.timeline.usecase.ObserveTimelineSettingsUseCase
import io.github.openflocon.domain.timeline.usecase.ObserveTimelineSummaryUseCase
import io.github.openflocon.domain.timeline.usecase.ObserveTimelineUseCase
import io.github.openflocon.domain.timeline.usecase.SetTimelineAutoScrollUseCase
import io.github.openflocon.domain.timeline.usecase.SetTimelineInvertListUseCase
import io.github.openflocon.domain.timeline.usecase.SetTimelinePinPanelUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val timelineDomainModule = module {
    factoryOf(::ObserveTimelineUseCase)
    factoryOf(::ObserveTimelineSummaryUseCase)
    factoryOf(::ObserveTimelineSettingsUseCase)
    factoryOf(::SetTimelineAutoScrollUseCase)
    factoryOf(::SetTimelineInvertListUseCase)
    factoryOf(::SetTimelinePinPanelUseCase)
}

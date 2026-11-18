package io.github.openflocon.domain.timelinelog

import io.github.openflocon.domain.timelinelog.usecase.ObserveTimelineLogUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val timelineLogModule = module {
    factoryOf(::ObserveTimelineLogUseCase)
}

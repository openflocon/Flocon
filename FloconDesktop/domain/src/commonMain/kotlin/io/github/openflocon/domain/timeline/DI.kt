package io.github.openflocon.domain.timeline

import io.github.openflocon.domain.timeline.usecase.ObserveTimelineUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val timelineDomainModule = module {
    factoryOf(::ObserveTimelineUseCase)
}

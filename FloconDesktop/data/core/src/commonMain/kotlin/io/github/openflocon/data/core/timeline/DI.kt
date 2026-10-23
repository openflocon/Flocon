package io.github.openflocon.data.core.timeline

import io.github.openflocon.data.core.timeline.repository.TimelineRepositoryImpl
import io.github.openflocon.domain.timeline.repository.TimelineRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val timelineModule = module {
    singleOf(::TimelineRepositoryImpl) {
        bind<TimelineRepository>()
    }
}

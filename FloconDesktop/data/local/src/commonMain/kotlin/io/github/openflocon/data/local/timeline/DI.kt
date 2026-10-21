package io.github.openflocon.data.local.timeline

import io.github.openflocon.data.core.timeline.datasources.TimelineLocalDataSource
import io.github.openflocon.data.local.timeline.datasource.TimelineLocalDataSourceRoom
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val timelineLocalModule = module {
    singleOf(::TimelineLocalDataSourceRoom) bind TimelineLocalDataSource::class
}

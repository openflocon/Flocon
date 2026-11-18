package io.github.openflocon.data.local.timelinelog

import io.github.openflocon.data.core.timelinelog.datasource.TimelineLogLocalDataSource
import io.github.openflocon.data.local.timelinelog.datasource.TimelineLogLocalDataSourceRoom
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val timelineLogLocalModule = module {
    singleOf(::TimelineLogLocalDataSourceRoom) bind TimelineLogLocalDataSource::class
}

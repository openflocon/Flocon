package com.flocon.data.remote.timelinelog

import com.flocon.data.remote.timelinelog.datasource.TimelineLogRemoteDataSourceImpl
import io.github.openflocon.data.core.timelinelog.datasource.TimelineLogRemoteDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val timelineLogModule = module {
    singleOf(::TimelineLogRemoteDataSourceImpl) bind TimelineLogRemoteDataSource::class
}

package io.github.openflocon.data.local.adb

import io.github.openflocon.data.core.adb.datasource.local.AdbLocalDataSource
import io.github.openflocon.data.local.adb.datasource.LocalAdbDataSourceRoom
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val adbModule = module {
    singleOf(::LocalAdbDataSourceRoom) bind AdbLocalDataSource::class
}

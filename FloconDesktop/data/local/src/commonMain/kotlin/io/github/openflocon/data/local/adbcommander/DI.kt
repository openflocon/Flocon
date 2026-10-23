package io.github.openflocon.data.local.adbcommander

import io.github.openflocon.data.core.adbcommander.datasource.AdbCommanderLocalDataSource
import io.github.openflocon.data.local.adbcommander.datasource.LocalAdbCommanderDataSourceRoom
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val adbCommanderModule = module {
    singleOf(::LocalAdbCommanderDataSourceRoom) bind AdbCommanderLocalDataSource::class
}

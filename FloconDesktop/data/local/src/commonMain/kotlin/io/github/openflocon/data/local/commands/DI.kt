package io.github.openflocon.data.local.commands

import io.github.openflocon.data.core.commands.AdbCommandLocalDataSource
import io.github.openflocon.data.local.commands.datasource.AdbCommandLocalDataSourceImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val adbCommandModule = module {
    singleOf(::AdbCommandLocalDataSourceImpl) bind AdbCommandLocalDataSource::class
}

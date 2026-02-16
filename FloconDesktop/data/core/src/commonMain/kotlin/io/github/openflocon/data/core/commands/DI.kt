package io.github.openflocon.data.core.commands

import io.github.openflocon.data.core.commands.repository.AdbCommandRepositoryImpl
import io.github.openflocon.domain.commands.repository.AdbCommandRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val adbCommandModule = module {
    singleOf(::AdbCommandRepositoryImpl) bind AdbCommandRepository::class
}

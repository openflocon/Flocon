package io.github.openflocon.data.core.adbcommander

import io.github.openflocon.data.core.adbcommander.repository.AdbCommanderRepositoryImpl
import io.github.openflocon.domain.adbcommander.repository.AdbCommanderRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val adbCommanderModule = module {
    singleOf(::AdbCommanderRepositoryImpl) {
        bind<AdbCommanderRepository>()
    }
}

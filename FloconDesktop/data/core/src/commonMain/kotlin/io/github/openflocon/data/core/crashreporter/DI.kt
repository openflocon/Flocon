package io.github.openflocon.data.core.crashreporter

import io.github.openflocon.data.core.crashreporter.repository.CrashReporterRepositoryImpl
import io.github.openflocon.domain.crashreporter.repository.CrashReporterRepository
import io.github.openflocon.domain.messages.repository.MessagesReceiverRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val crashReporterModule = module {
    singleOf(::CrashReporterRepositoryImpl) {
        bind<CrashReporterRepository>()
        bind<MessagesReceiverRepository>()
    }
}

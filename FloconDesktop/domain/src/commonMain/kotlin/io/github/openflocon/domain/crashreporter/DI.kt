package io.github.openflocon.domain.crashreporter

import io.github.openflocon.domain.crashreporter.usecase.ClearAllCrashReportUseCase
import io.github.openflocon.domain.crashreporter.usecase.DeleteCrashReportUseCase
import io.github.openflocon.domain.crashreporter.usecase.ObserveCrashReportsByIdUseCase
import io.github.openflocon.domain.crashreporter.usecase.ObserveCrashReportsUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val crashReporterDomainModule = module {
    factoryOf(::ObserveCrashReportsUseCase)
    factoryOf(::ObserveCrashReportsByIdUseCase)
    factoryOf(::DeleteCrashReportUseCase)
    factoryOf(::ClearAllCrashReportUseCase)
}

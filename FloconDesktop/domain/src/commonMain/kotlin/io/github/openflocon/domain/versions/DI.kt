package io.github.openflocon.domain.versions

import io.github.openflocon.domain.versions.usecase.CheckIsDesktopOnLastVersionUseCase
import io.github.openflocon.domain.versions.usecase.ObserveIsClientOnLastVersionUseCase
import io.github.openflocon.domain.versions.usecase.ObserveLastAvailableFloconVersionUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val versionModule = module {
    factoryOf(::CheckIsDesktopOnLastVersionUseCase)
    factoryOf(::ObserveLastAvailableFloconVersionUseCase)
    factoryOf(::ObserveIsClientOnLastVersionUseCase)
}

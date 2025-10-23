package io.github.openflocon.domain.versions

import io.github.openflocon.domain.versions.usecase.CheckIsLastVersionUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val versionModule = module {
    factoryOf(::CheckIsLastVersionUseCase)
}

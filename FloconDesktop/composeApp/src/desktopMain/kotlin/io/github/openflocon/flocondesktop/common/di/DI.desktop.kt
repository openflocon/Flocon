package io.github.openflocon.flocondesktop.common.di

import io.github.openflocon.domain.settings.usecase.IosExecutor
import io.github.openflocon.flocondesktop.common.IosExecutorDesktop
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val platformModule = module {
    singleOf(::IosExecutorDesktop) bind IosExecutor::class
}

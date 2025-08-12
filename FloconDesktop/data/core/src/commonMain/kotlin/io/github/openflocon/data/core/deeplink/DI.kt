package io.github.openflocon.data.core.deeplink

import io.github.openflocon.data.core.deeplink.repository.DeeplinkRepositoryImpl
import io.github.openflocon.domain.deeplink.repository.DeeplinkRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val deeplinkModule = module {
    singleOf(::DeeplinkRepositoryImpl) bind DeeplinkRepository::class
}

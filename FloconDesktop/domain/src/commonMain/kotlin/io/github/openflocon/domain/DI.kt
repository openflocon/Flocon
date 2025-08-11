package io.github.openflocon.domain

import io.github.openflocon.domain.network.networkModule
import org.koin.dsl.module

val domainModule = module {
    includes(networkModule)
}

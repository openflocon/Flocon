package io.github.openflocon.data.core.network

import io.github.openflocon.data.core.network.repository.NetworkFilterRepositoryImpl
import io.github.openflocon.data.core.network.repository.NetworkRepositoryImpl
import io.github.openflocon.domain.network.repository.NetworkFilterRepository
import io.github.openflocon.domain.network.repository.NetworkRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val networkModule = module {
    singleOf(::NetworkFilterRepositoryImpl) bind NetworkFilterRepository::class
    singleOf(::NetworkRepositoryImpl) bind NetworkRepository::class
}

package io.github.openflocon.flocondesktop.features.network.data.di

import io.github.openflocon.flocondesktop.features.network.data.NetworkFilterRepositoryImpl
import io.github.openflocon.flocondesktop.features.network.data.NetworkRepositoryImpl
import io.github.openflocon.domain.network.repository.NetworkFilterRepository
import io.github.openflocon.domain.network.repository.NetworkMocksRepository
import io.github.openflocon.domain.network.repository.NetworkRepository
import io.github.openflocon.domain.messages.repository.MessagesReceiverRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val networkDataModule =
    module {
        singleOf(::NetworkRepositoryImpl) {
            bind<NetworkRepository>()
            bind<MessagesReceiverRepository>()
            bind<NetworkMocksRepository>()
        }
        singleOf(::NetworkFilterRepositoryImpl) {
            bind<NetworkFilterRepository>()
        }
    }

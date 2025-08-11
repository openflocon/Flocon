package io.github.openflocon.flocondesktop.features.network.data.di

import io.github.openflocon.flocondesktop.features.network.data.NetworkFilterRepositoryImpl
import io.github.openflocon.flocondesktop.features.network.data.NetworkRepositoryImpl
import io.github.openflocon.data.core.network.datasource.NetworkFilterLocalDataSource
import io.github.openflocon.flocondesktop.features.network.data.datasource.local.NetworkFilterLocalDataSourceRoom
import io.github.openflocon.data.core.network.datasource.NetworkLocalDataSource
import io.github.openflocon.flocondesktop.features.network.data.datasource.local.NetworkLocalDataSourceRoom
import io.github.openflocon.domain.network.repository.NetworkFilterRepository
import io.github.openflocon.domain.network.repository.NetworkRepository
import io.github.openflocon.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val networkDataModule =
    module {
        singleOf(::NetworkRepositoryImpl) {
            bind<NetworkRepository>()
            bind<MessagesReceiverRepository>()
        }
        singleOf(::NetworkLocalDataSourceRoom) {
            bind<NetworkLocalDataSource>()
        }
        singleOf(::NetworkFilterRepositoryImpl) {
            bind<NetworkFilterRepository>()
        }
        singleOf(::NetworkFilterLocalDataSourceRoom) {
            bind<NetworkFilterLocalDataSource>()
        }
    }

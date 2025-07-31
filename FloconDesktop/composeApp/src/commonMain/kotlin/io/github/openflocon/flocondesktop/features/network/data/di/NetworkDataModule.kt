package io.github.openflocon.flocondesktop.features.network.data.di

import io.github.openflocon.flocondesktop.features.network.data.NetworkRepositoryImpl
import io.github.openflocon.flocondesktop.features.network.data.datasource.local.NetworkLocalDataSource
import io.github.openflocon.flocondesktop.features.network.data.datasource.local.NetworkLocalDataSourceRoom
import io.github.openflocon.flocondesktop.features.network.domain.repository.NetworkRepository
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
    }

package io.github.openflocon.flocondesktop.features.network.data.di

import com.florent37.flocondesktop.features.network.data.NetworkRepositoryImpl
import com.florent37.flocondesktop.features.network.data.datasource.local.NetworkLocalDataSource
import com.florent37.flocondesktop.features.network.data.datasource.local.NetworkLocalDataSourceRoom
import com.florent37.flocondesktop.features.network.domain.repository.NetworkRepository
import com.florent37.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
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

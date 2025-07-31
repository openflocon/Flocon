package io.github.openflocon.flocondesktop.features.deeplinks.data.di

import com.florent37.flocondesktop.features.deeplinks.data.DeeplinkRepositoryImpl
import com.florent37.flocondesktop.features.deeplinks.data.datasource.LocalDeeplinkDataSource
import com.florent37.flocondesktop.features.deeplinks.data.datasource.room.LocalDeeplinkDataSourceRoom
import com.florent37.flocondesktop.features.deeplinks.domain.repository.DeeplinkRepository
import com.florent37.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val deeplinkDataModule =
    module {
        factoryOf(::DeeplinkRepositoryImpl) {
            bind<DeeplinkRepository>()
            bind<MessagesReceiverRepository>()
        }
        singleOf(::LocalDeeplinkDataSourceRoom) {
            bind<LocalDeeplinkDataSource>()
        }
    }

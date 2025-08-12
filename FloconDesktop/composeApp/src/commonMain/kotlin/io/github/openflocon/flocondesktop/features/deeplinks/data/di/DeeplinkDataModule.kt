package io.github.openflocon.flocondesktop.features.deeplinks.data.di

import io.github.openflocon.domain.deeplink.repository.DeeplinkRepository
import io.github.openflocon.flocondesktop.features.deeplinks.data.DeeplinkRepositoryImpl
import io.github.openflocon.domain.messages.repository.MessagesReceiverRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val deeplinkDataModule =
    module {
        factoryOf(::DeeplinkRepositoryImpl) {
            bind<DeeplinkRepository>()
            bind<MessagesReceiverRepository>()
        }
    }

package io.github.openflocon.flocondesktop.messages.data.di

import io.github.openflocon.flocondesktop.Server
import io.github.openflocon.flocondesktop.getServer
import io.github.openflocon.flocondesktop.messages.data.MessagesRepositoryImpl
import io.github.openflocon.flocondesktop.messages.domain.repository.MessagesRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val messagesDataModule =
    module {
        single<Server> {
            getServer()
        }
        singleOf(::MessagesRepositoryImpl) {
            bind<MessagesRepository>()
        }
    }

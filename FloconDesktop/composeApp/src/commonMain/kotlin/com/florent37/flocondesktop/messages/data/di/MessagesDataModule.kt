package com.florent37.flocondesktop.messages.data.di

import com.florent37.flocondesktop.Server
import com.florent37.flocondesktop.getServer
import com.florent37.flocondesktop.messages.data.MessagesRepositoryImpl
import com.florent37.flocondesktop.messages.domain.repository.MessagesRepository
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

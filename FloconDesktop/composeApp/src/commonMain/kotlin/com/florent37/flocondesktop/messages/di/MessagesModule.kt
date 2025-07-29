package com.florent37.flocondesktop.messages.di

import com.florent37.flocondesktop.Server
import com.florent37.flocondesktop.getServer
import com.florent37.flocondesktop.messages.data.di.messagesDataModule
import com.florent37.flocondesktop.messages.domain.di.messagesDomainModule
import com.florent37.flocondesktop.messages.ui.di.messagesUiModule
import org.koin.dsl.module

val messagesModule =
    module {
        single<Server> {
            getServer()
        }
        includes(
            messagesDomainModule,
            messagesDataModule,
            messagesUiModule,
        )
    }

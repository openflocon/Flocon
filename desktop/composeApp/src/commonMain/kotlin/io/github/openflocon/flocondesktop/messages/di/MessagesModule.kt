package io.github.openflocon.flocondesktop.messages.di

import io.github.openflocon.flocondesktop.Server
import io.github.openflocon.flocondesktop.getServer
import io.github.openflocon.flocondesktop.messages.data.di.messagesDataModule
import io.github.openflocon.flocondesktop.messages.domain.di.messagesDomainModule
import io.github.openflocon.flocondesktop.messages.ui.di.messagesUiModule
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

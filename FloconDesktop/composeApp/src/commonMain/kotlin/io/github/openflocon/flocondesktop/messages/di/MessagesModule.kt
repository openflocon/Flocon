package io.github.openflocon.flocondesktop.messages.di

import io.github.openflocon.flocondesktop.messages.ui.di.messagesUiModule
import org.koin.dsl.module

val messagesModule = module {
    includes(
        messagesUiModule,
    )
}

package io.github.openflocon.flocondesktop.messages.ui.di

import io.github.openflocon.flocondesktop.messages.ui.MessagesServerDelegate
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val messagesUiModule =
    module {
        singleOf(::MessagesServerDelegate)
    }

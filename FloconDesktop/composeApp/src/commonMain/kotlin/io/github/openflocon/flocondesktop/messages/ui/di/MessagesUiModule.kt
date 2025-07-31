package io.github.openflocon.flocondesktop.messages.ui.di

import com.florent37.flocondesktop.messages.ui.MessagesServerDelegate
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val messagesUiModule =
    module {
        factoryOf(::MessagesServerDelegate)
    }

package io.github.openflocon.flocondesktop.messages.domain.di

import com.florent37.flocondesktop.messages.domain.HandleIncomingMessagesUseCase
import com.florent37.flocondesktop.messages.domain.StartServerUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val messagesDomainModule =
    module {
        factory {
            HandleIncomingMessagesUseCase(
                messagesRepository = get(),
                plugins = getAll(),
                handleDeviceUseCase = get(),
            )
        }
        factoryOf(::StartServerUseCase)
    }

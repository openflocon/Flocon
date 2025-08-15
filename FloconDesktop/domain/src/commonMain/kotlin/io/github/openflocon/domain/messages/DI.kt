package io.github.openflocon.domain.messages

import io.github.openflocon.domain.messages.usecase.HandleIncomingMessagesUseCase
import io.github.openflocon.domain.messages.usecase.StartServerUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val messagesModule = module {
    factory {
        HandleIncomingMessagesUseCase(
            messagesRepository = get(),
            plugins = getAll(),
            handleDeviceUseCase = get(),
            handleNewDeviceUseCase = get(),
        )
    }
    factoryOf(::StartServerUseCase)
}

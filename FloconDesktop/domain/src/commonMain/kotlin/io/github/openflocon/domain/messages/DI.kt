package io.github.openflocon.domain.messages

import io.github.openflocon.domain.messages.usecase.HandleIncomingMessagesUseCase
import io.github.openflocon.domain.messages.usecase.HandleReceivedFilesUseCase
import io.github.openflocon.domain.messages.usecase.StartServerUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val messagesModule = module {
    factory {
        HandleIncomingMessagesUseCase(
            messagesRepository = get(),
            plugins = getAll(),
            handleDeviceAndAppUseCase = get(),
            handleNewDeviceUseCase = get(),
            handleNewAppUseCase = get(),
        )
    }
    factory {
        HandleReceivedFilesUseCase(
            messagesRepository = get(),
            plugins = getAll(),
        )
    }
    factoryOf(::StartServerUseCase)
}

package io.github.openflocon.data.core.messages

import io.github.openflocon.data.core.messages.repository.MessagesRepositoryImpl
import io.github.openflocon.domain.messages.repository.MessagesRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val messageModule = module {
    singleOf(::MessagesRepositoryImpl) bind MessagesRepository::class
}

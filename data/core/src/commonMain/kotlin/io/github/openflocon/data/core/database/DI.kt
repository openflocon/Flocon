package io.github.openflocon.data.core.database

import io.github.openflocon.data.core.database.repository.DatabaseRepositoryImpl
import io.github.openflocon.domain.database.repository.DatabaseRepository
import io.github.openflocon.domain.messages.repository.MessagesReceiverRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val databaseModule = module {
    singleOf(::DatabaseRepositoryImpl) {
        bind<DatabaseRepository>()
        bind<MessagesReceiverRepository>()
    }
}

package io.github.openflocon.data.core.table

import io.github.openflocon.data.core.table.repository.TableRepositoryImpl
import io.github.openflocon.domain.messages.repository.MessagesReceiverRepository
import io.github.openflocon.domain.table.repository.TableRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val tableModule = module {
    singleOf(::TableRepositoryImpl) {
        bind<TableRepository>()
        bind<MessagesReceiverRepository>()
    }
}

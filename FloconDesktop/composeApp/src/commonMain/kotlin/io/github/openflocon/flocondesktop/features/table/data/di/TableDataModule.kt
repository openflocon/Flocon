package io.github.openflocon.flocondesktop.features.table.data.di

import io.github.openflocon.domain.table.repository.TableRepository
import io.github.openflocon.flocondesktop.features.table.data.TableRepositoryImpl
import io.github.openflocon.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val tableDataModule = module {
    singleOf(::TableRepositoryImpl) {
        bind<TableRepository>()
        bind<MessagesReceiverRepository>()
    }
}

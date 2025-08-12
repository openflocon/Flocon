package io.github.openflocon.flocondesktop.features.dashboard.data.di

import io.github.openflocon.domain.dashboard.repository.DashboardRepository
import io.github.openflocon.domain.messages.repository.MessagesReceiverRepository
import io.github.openflocon.data.core.dashboard.repository.DashboardRepositoryImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val dashboardDataModule = module {
    factoryOf(::DashboardRepositoryImpl) {
        bind<DashboardRepository>()
        bind<MessagesReceiverRepository>()
    }
}

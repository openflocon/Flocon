package io.github.openflocon.data.core.dashboard

import io.github.openflocon.data.core.dashboard.repository.DashboardRepositoryImpl
import io.github.openflocon.domain.dashboard.repository.DashboardRepository
import io.github.openflocon.domain.messages.repository.MessagesReceiverRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val dashboardModule = module {
    factoryOf(::DashboardRepositoryImpl) {
        bind<DashboardRepository>()
        bind<MessagesReceiverRepository>()
    }
}

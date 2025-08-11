package io.github.openflocon.domain

import io.github.openflocon.domain.dashboard.dashboardModule
import io.github.openflocon.domain.database.databaseModule
import io.github.openflocon.domain.network.networkModule
import org.koin.dsl.module

val domainModule = module {
    includes(
        networkModule,
        dashboardModule,
        databaseModule
    )
}

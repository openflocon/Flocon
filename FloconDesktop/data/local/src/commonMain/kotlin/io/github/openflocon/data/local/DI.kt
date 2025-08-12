package io.github.openflocon.data.local

import io.github.openflocon.data.local.analytics.analyticsModule
import io.github.openflocon.data.local.dashboard.dashboardModule
import io.github.openflocon.data.local.database.databaseModule
import io.github.openflocon.data.local.network.networkModule
import org.koin.dsl.module

val dataLocalModule = module {
    includes(
        analyticsModule,
        dashboardModule,
        databaseModule,
        networkModule
    )
}

package io.github.openflocon.data.core

import io.github.openflocon.data.core.analytics.analyticsModule
import io.github.openflocon.data.core.dashboard.dashboardModule
import io.github.openflocon.data.core.database.databaseModule
import org.koin.dsl.module

val dataCoreModule = module {
    includes(
        analyticsModule,
        dashboardModule,
        databaseModule
    )
}

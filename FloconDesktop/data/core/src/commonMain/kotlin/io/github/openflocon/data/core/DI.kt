package io.github.openflocon.data.core

import io.github.openflocon.data.core.analytics.analyticsModule
import io.github.openflocon.data.core.dashboard.dashboardModule
import io.github.openflocon.data.core.database.databaseModule
import io.github.openflocon.data.core.deeplink.deeplinkModule
import io.github.openflocon.data.core.files.filesModule
import io.github.openflocon.data.core.images.imagesModule
import io.github.openflocon.data.core.sharedpreference.sharedPreferenceModule
import org.koin.dsl.module

val dataCoreModule = module {
    includes(
        analyticsModule,
        dashboardModule,
        databaseModule,
        deeplinkModule,
        filesModule,
        imagesModule,
        sharedPreferenceModule
    )
}

package io.github.openflocon.data.local

import io.github.openflocon.data.local.adb.adbModule
import io.github.openflocon.data.local.analytics.analyticsModule
import io.github.openflocon.data.local.dashboard.dashboardModule
import io.github.openflocon.data.local.database.databaseModule
import io.github.openflocon.data.local.deeplink.deeplinkModule
import io.github.openflocon.data.local.device.deviceModule
import io.github.openflocon.data.local.files.filesModule
import io.github.openflocon.data.local.images.imagesModule
import io.github.openflocon.data.local.network.networkModule
import io.github.openflocon.data.local.sharedpreference.sharedPreferenceModule
import io.github.openflocon.data.local.table.tableModule
import org.koin.dsl.module

val dataLocalModule = module {
    includes(
        adbModule,
        analyticsModule,
        dashboardModule,
        databaseModule,
        deeplinkModule,
        deviceModule,
        filesModule,
        imagesModule,
        networkModule,
        sharedPreferenceModule,
        tableModule
    )
}

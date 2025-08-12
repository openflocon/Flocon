package io.github.openflocon.domain

import io.github.openflocon.domain.analytics.analyticsModule
import io.github.openflocon.domain.dashboard.dashboardModule
import io.github.openflocon.domain.database.databaseModule
import io.github.openflocon.domain.deeplink.deeplinkModule
import io.github.openflocon.domain.device.deviceModule
import io.github.openflocon.domain.files.filesModule
import io.github.openflocon.domain.images.imagesModule
import io.github.openflocon.domain.messages.messagesModule
import io.github.openflocon.domain.network.networkModule
import io.github.openflocon.domain.settings.settingsModule
import io.github.openflocon.domain.sharedpreference.sharedPreferencesModule
import io.github.openflocon.domain.table.tableModule
import org.koin.dsl.module

val domainModule = module {
    includes(
        analyticsModule,
        dashboardModule,
        databaseModule,
        deeplinkModule,
        deviceModule,
        filesModule,
        imagesModule,
        networkModule,
        settingsModule,
        sharedPreferencesModule,
        tableModule,
        messagesModule
    )
}

package io.github.openflocon.flocondesktop.features

import io.github.openflocon.flocondesktop.features.analytics.analyticsModule
import io.github.openflocon.flocondesktop.features.dashboard.dashboardModule
import io.github.openflocon.flocondesktop.features.database.databaseModule
import io.github.openflocon.flocondesktop.features.deeplinks.deeplinkModule
import io.github.openflocon.flocondesktop.features.files.filesModule
import io.github.openflocon.flocondesktop.features.images.imagesModule
import io.github.openflocon.flocondesktop.features.network.networkModule
import io.github.openflocon.flocondesktop.features.sharedpreferences.sharedPreferencesModule
import io.github.openflocon.flocondesktop.features.table.di.tableModule
import io.github.openflocon.flocondesktop.messages.di.messagesModule
import org.koin.dsl.module

val featuresModule = module {
    includes(
        analyticsModule,
        databaseModule,
        filesModule,
        imagesModule,
        messagesModule,
        networkModule,
        sharedPreferencesModule,
        dashboardModule,
        tableModule,
        deeplinkModule,
    )
}

package io.github.openflocon.flocondesktop.features

import io.github.openflocon.flocondesktop.features.analytics.di.analyticsModule
import io.github.openflocon.flocondesktop.features.dashboard.di.dashboardModule
import io.github.openflocon.flocondesktop.features.database.di.databaseModule
import io.github.openflocon.flocondesktop.features.deeplinks.di.deeplinkModule
import io.github.openflocon.flocondesktop.features.files.di.filesModule
import io.github.openflocon.flocondesktop.features.images.di.imagesModule
import io.github.openflocon.flocondesktop.features.network.di.networkModule
import io.github.openflocon.flocondesktop.features.sharedpreferences.di.sharedPreferencesModule
import io.github.openflocon.flocondesktop.features.table.di.tableModule
import io.github.openflocon.flocondesktop.messages.di.messagesModule
import org.koin.dsl.module

val featuresModule =
    module {
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

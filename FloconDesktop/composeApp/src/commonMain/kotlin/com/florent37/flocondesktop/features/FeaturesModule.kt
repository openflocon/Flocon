package com.florent37.flocondesktop.features

import com.florent37.flocondesktop.features.analytics.di.analyticsModule
import com.florent37.flocondesktop.features.dashboard.di.dashboardModule
import com.florent37.flocondesktop.features.database.di.databaseModule
import com.florent37.flocondesktop.features.deeplinks.di.deeplinkModule
import com.florent37.flocondesktop.features.files.di.filesModule
import com.florent37.flocondesktop.features.grpc.di.grpcModule
import com.florent37.flocondesktop.features.images.di.imagesModule
import com.florent37.flocondesktop.features.network.di.networkModule
import com.florent37.flocondesktop.features.sharedpreferences.di.sharedPreferencesModule
import com.florent37.flocondesktop.features.table.di.tableModule
import com.florent37.flocondesktop.messages.di.messagesModule
import org.koin.dsl.module

val featuresModule =
    module {
        includes(
            analyticsModule,
            databaseModule,
            filesModule,
            grpcModule,
            imagesModule,
            messagesModule,
            networkModule,
            sharedPreferencesModule,
            dashboardModule,
            tableModule,
            deeplinkModule,
        )
    }

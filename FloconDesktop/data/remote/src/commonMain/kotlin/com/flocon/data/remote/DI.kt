package com.flocon.data.remote

import com.flocon.data.remote.analytics.analyticsModule
import com.flocon.data.remote.dashboard.dashboardModule
import com.flocon.data.remote.database.databaseModule
import com.flocon.data.remote.deeplink.deeplinkModule
import com.flocon.data.remote.device.deviceModule
import com.flocon.data.remote.files.filesModule
import com.flocon.data.remote.messages.messagesModule
import com.flocon.data.remote.network.networkModule
import com.flocon.data.remote.sharedpreference.sharedPreferencesModule
import com.flocon.data.remote.table.tableModule
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val dataRemoteModule = module {
    includes(
        analyticsModule,
        dashboardModule,
        databaseModule,
        deeplinkModule,
        filesModule,
        messagesModule,
        networkModule,
        sharedPreferencesModule,
        tableModule,
        deviceModule,
    )

    single {
        Json {
            ignoreUnknownKeys = true
        }
    }
}

package com.flocon.data.remote

import com.flocon.data.remote.analytics.analyticsModule
import com.flocon.data.remote.database.databaseModule
import com.flocon.data.remote.files.filesModule
import com.flocon.data.remote.network.networkModule
import com.flocon.data.remote.sharedpreference.sharedPreferencesModule
import com.flocon.data.remote.table.tableModule
import org.koin.dsl.module

val dataRemoteModule = module {
    includes(
        analyticsModule,
        databaseModule,
        filesModule,
        networkModule,
        sharedPreferencesModule,
        tableModule,
    )
}

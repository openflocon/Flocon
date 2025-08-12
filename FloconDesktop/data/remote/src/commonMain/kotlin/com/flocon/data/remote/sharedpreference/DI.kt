package com.flocon.data.remote.sharedpreference

import com.flocon.data.remote.sharedpreference.datasource.DeviceSharedPreferencesRemoteDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val sharedPreferencesModule = module {
    singleOf(::DeviceSharedPreferencesRemoteDataSource)
}

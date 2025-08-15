package com.flocon.data.remote.sharedpreference

import com.flocon.data.remote.sharedpreference.datasource.DeviceSharedPreferencesRemoteDataSourceImpl
import io.github.openflocon.data.core.sharedpreference.datasource.DeviceSharedPreferencesRemoteDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val sharedPreferencesModule = module {
    singleOf(::DeviceSharedPreferencesRemoteDataSourceImpl) bind DeviceSharedPreferencesRemoteDataSource::class
}

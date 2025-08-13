package com.flocon.data.remote.network

import com.flocon.data.remote.network.datasource.NetworkRemoteDataSourceImpl
import io.github.openflocon.data.core.network.datasource.NetworkRemoteDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val networkModule = module {
    singleOf(::NetworkRemoteDataSourceImpl) bind NetworkRemoteDataSource::class
}

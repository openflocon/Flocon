package com.flocon.data.remote.network

import com.flocon.data.remote.network.datasource.NetworkRemoteDataSourceImpl
import com.flocon.data.remote.network.datasource.NetworkReplayDataSourceImpl
import io.github.openflocon.data.core.network.datasource.NetworkRemoteDataSource
import io.github.openflocon.data.core.network.datasource.NetworkReplayDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val networkModule = module {
    singleOf(::NetworkRemoteDataSourceImpl) bind NetworkRemoteDataSource::class
    singleOf(::NetworkReplayDataSourceImpl) bind NetworkReplayDataSource::class
}

package com.flocon.data.remote.deeplink

import com.flocon.data.remote.deeplink.datasource.DeeplinkRemoteDataSourceImpl
import io.github.openflocon.data.core.deeplink.datasource.DeeplinkRemoteDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val deeplinkModule = module {
    singleOf(::DeeplinkRemoteDataSourceImpl) bind DeeplinkRemoteDataSource::class
}

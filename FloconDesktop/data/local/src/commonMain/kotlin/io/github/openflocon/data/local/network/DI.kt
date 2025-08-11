package io.github.openflocon.data.local.network

import io.github.openflocon.data.core.network.datasource.NetworkFilterLocalDataSource
import io.github.openflocon.data.core.network.datasource.NetworkLocalDataSource
import io.github.openflocon.data.local.network.datasource.NetworkFilterLocalDataSourceRoom
import io.github.openflocon.data.local.network.datasource.NetworkLocalDataSourceRoom
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val networkModule = module {
    singleOf(::NetworkLocalDataSourceRoom) bind NetworkLocalDataSource::class
    singleOf(::NetworkFilterLocalDataSourceRoom) bind NetworkFilterLocalDataSource::class
}

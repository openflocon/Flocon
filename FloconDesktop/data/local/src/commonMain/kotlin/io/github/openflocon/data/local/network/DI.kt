package io.github.openflocon.data.local.network

import io.github.openflocon.data.core.network.datasource.NetworkFilterLocalDataSource
import io.github.openflocon.data.core.network.datasource.NetworkLocalDataSource
import io.github.openflocon.data.core.network.datasource.NetworkLocalWebsocketDataSource
import io.github.openflocon.data.core.network.datasource.NetworkMocksLocalDataSource
import io.github.openflocon.data.core.network.datasource.NetworkQualityLocalDataSource
import io.github.openflocon.data.core.network.datasource.NetworkSettingsLocalDataSource
import io.github.openflocon.data.local.network.datasource.BadQualityConfigLocalDataSourceImpl
import io.github.openflocon.data.local.network.datasource.NetworkFilterLocalDataSourceRoom
import io.github.openflocon.data.local.network.datasource.NetworkLocalDataSourceRoom
import io.github.openflocon.data.local.network.datasource.NetworkLocalWebsocketDataSourceRam
import io.github.openflocon.data.local.network.datasource.NetworkMocksLocalDataSourceImpl
import io.github.openflocon.data.local.network.datasource.NetworkSettingsLocalDataSourceRoom
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val networkModule = module {
    singleOf(::NetworkLocalDataSourceRoom) bind NetworkLocalDataSource::class
    singleOf(::NetworkFilterLocalDataSourceRoom) bind NetworkFilterLocalDataSource::class
    singleOf(::NetworkMocksLocalDataSourceImpl) bind NetworkMocksLocalDataSource::class
    singleOf(::BadQualityConfigLocalDataSourceImpl) bind NetworkQualityLocalDataSource::class
    singleOf(::NetworkSettingsLocalDataSourceRoom) bind NetworkSettingsLocalDataSource::class
    singleOf(::NetworkLocalWebsocketDataSourceRam) bind NetworkLocalWebsocketDataSource::class
}

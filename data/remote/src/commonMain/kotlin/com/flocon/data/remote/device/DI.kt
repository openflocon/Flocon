package com.flocon.data.remote.device

import com.flocon.data.remote.device.datasource.DeviceRemoteDataSourceImpl
import io.github.openflocon.data.core.device.datasource.remote.RemoteDeviceDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val deviceModule = module {
    singleOf(::DeviceRemoteDataSourceImpl) bind RemoteDeviceDataSource::class
}

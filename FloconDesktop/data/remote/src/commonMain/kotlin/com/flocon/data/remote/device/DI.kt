package com.flocon.data.remote.device

import com.flocon.data.remote.device.datasource.DeviceRemoteDataSourceImpl
import com.flocon.data.remote.files.datasource.FilesRemoteDataSourceImpl
import io.github.openflocon.data.core.device.datasource.remote.RemoteDeviceDataSource
import io.github.openflocon.data.core.files.datasource.FilesRemoteDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val deviceModule = module {
    singleOf(::DeviceRemoteDataSourceImpl) bind RemoteDeviceDataSource::class
}

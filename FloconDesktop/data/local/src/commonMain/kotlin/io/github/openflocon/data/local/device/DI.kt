package io.github.openflocon.data.local.device

import io.github.openflocon.data.core.device.datasource.local.LocalCurrentDeviceDataSource
import io.github.openflocon.data.core.device.datasource.local.LocalDevicesDataSource
import io.github.openflocon.data.local.device.datasource.local.LocalCurrentDeviceDataSourceInMemory
import io.github.openflocon.data.local.device.datasource.local.LocalDevicesDataSourceRoom
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val deviceModule = module {
    singleOf(::LocalCurrentDeviceDataSourceInMemory) bind LocalCurrentDeviceDataSource::class
    singleOf(::LocalDevicesDataSourceRoom) bind LocalDevicesDataSource::class
}

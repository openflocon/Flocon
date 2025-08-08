package io.github.openflocon.flocondesktop.core.domain.device.repository

import io.github.openflocon.flocondesktop.messages.domain.model.DeviceAppDomainModel
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceDomainModel
import kotlinx.coroutines.flow.Flow

interface DevicesRepository {
    val devices: Flow<List<DeviceDomainModel>>

    suspend fun register(device: DeviceDomainModel)

    suspend fun unregister(device: DeviceDomainModel)

    suspend fun clear()

    val currentDevice: Flow<DeviceDomainModel?>

    val currentDeviceApp: Flow<DeviceAppDomainModel?>

    fun getCurrentDevice(): DeviceDomainModel?

    fun getCurrentDeviceApp(): DeviceAppDomainModel?

    suspend fun selectDevice(device: DeviceDomainModel)

    suspend fun selectApp(app: DeviceAppDomainModel)

}

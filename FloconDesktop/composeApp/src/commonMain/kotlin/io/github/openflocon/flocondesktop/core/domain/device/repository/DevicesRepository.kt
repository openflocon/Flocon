package io.github.openflocon.flocondesktop.core.domain.device.repository

import com.flocon.library.domain.models.DeviceAppDomainModel
import com.flocon.library.domain.models.DeviceDomainModel
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

package io.github.openflocon.data.core.device.datasource.local

import io.github.openflocon.domain.device.models.DeviceAppDomainModel
import io.github.openflocon.domain.device.models.DeviceId
import kotlinx.coroutines.flow.Flow

interface LocalCurrentDeviceDataSource {
    val currentDeviceId: Flow<DeviceId?>
    suspend fun getCurrentDeviceId(): DeviceId?
    suspend fun selectDevice(deviceId: DeviceId)

    val currentDeviceApp: Flow<DeviceAppDomainModel?>
    suspend fun getCurrentDeviceApp(): DeviceAppDomainModel?
    suspend fun selectApp(app: DeviceAppDomainModel)

    suspend fun addNewDeviceConnectedForThisSession(deviceId: DeviceId)
    suspend fun isKnownDeviceForThisSession(deviceId: DeviceId): Boolean

    suspend fun clear()
}

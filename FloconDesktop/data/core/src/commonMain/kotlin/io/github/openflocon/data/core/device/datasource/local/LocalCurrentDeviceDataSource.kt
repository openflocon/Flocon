package io.github.openflocon.data.core.device.datasource.local

import io.github.openflocon.domain.device.models.DeviceAppDomainModel
import io.github.openflocon.domain.device.models.DeviceId
import kotlinx.coroutines.flow.Flow

interface LocalCurrentDeviceDataSource {
    val currentDeviceId: Flow<DeviceId?>
    suspend fun getCurrentDeviceId(): DeviceId?
    suspend fun selectDevice(deviceId: DeviceId)

    suspend fun addNewDeviceConnectedForThisSession(deviceId: DeviceId)
    suspend fun isKnownDeviceForThisSession(deviceId: DeviceId): Boolean

    // region app
    fun observeDeviceSelectedApp(deviceId: DeviceId): Flow<DeviceAppDomainModel?>
    suspend fun getDeviceSelectedApp(deviceId: DeviceId): DeviceAppDomainModel?
    suspend fun selectApp(deviceId: DeviceId, app: DeviceAppDomainModel)
    suspend fun isKnownAppForThisSession(deviceId: DeviceId, packageName: String) : Boolean
    suspend fun addNewDeviceAppConnectedForThisSession(deviceId: DeviceId, packageName: String)
    // endregion

    suspend fun clear()
}

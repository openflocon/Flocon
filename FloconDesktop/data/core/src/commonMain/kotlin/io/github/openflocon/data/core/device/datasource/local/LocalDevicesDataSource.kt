package io.github.openflocon.data.core.device.datasource.local

import io.github.openflocon.data.core.device.datasource.local.model.InsertResult
import io.github.openflocon.domain.device.models.DeviceAppDomainModel
import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.models.DeviceId
import kotlinx.coroutines.flow.Flow


interface LocalDevicesDataSource {
    // region device
    val devices: Flow<List<DeviceDomainModel>>
    fun observeDeviceById(it: DeviceId): Flow<DeviceDomainModel?>
    suspend fun getDeviceById(it: DeviceId): DeviceDomainModel?
    suspend fun insertDevice(device: DeviceDomainModel) : InsertResult
    // endregion

    // region apps
    suspend fun insertDeviceApp(deviceId: DeviceId, app: DeviceAppDomainModel) : InsertResult
    fun observeDeviceApps(deviceId: DeviceId) : Flow<List<DeviceAppDomainModel>>
    suspend fun getDeviceAppByPackage(deviceId: DeviceId, packageName: String) : DeviceAppDomainModel?
    // endregion

    suspend fun clear()
}


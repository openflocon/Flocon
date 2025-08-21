package io.github.openflocon.domain.device.repository

import io.github.openflocon.domain.device.models.DeviceAppDomainModel
import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.device.models.DeviceWithAppsDomainModel
import io.github.openflocon.domain.device.models.DeviceWithAppDomainModel
import io.github.openflocon.domain.device.models.HandleDeviceResultDomainModel
import io.github.openflocon.domain.device.models.RegisterDeviceWithAppDomainModel
import kotlinx.coroutines.flow.Flow

interface DevicesRepository {
    val devices: Flow<List<DeviceDomainModel>>
    val currentDeviceWithApp: Flow<DeviceWithAppDomainModel?>
    val currentDeviceId: Flow<DeviceId?>

    fun observeDeviceApps(deviceId: DeviceId): Flow<List<DeviceAppDomainModel>>

    suspend fun getCurrentDeviceId(): DeviceId?

    // returns new if new device
    suspend fun register(registerDeviceWithApp: RegisterDeviceWithAppDomainModel) : HandleDeviceResultDomainModel

    suspend fun getCurrentDevice(): DeviceDomainModel?

    suspend fun getCurrentDeviceApp(): DeviceAppDomainModel?

    suspend fun selectDevice(deviceId: DeviceId)

    suspend fun getDeviceAppByPackage(deviceId: DeviceId, appPackageName: String) : DeviceAppDomainModel?
    suspend fun selectApp(app: DeviceAppDomainModel)

    suspend fun clear()
}

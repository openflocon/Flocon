package io.github.openflocon.domain.device.repository

import io.github.openflocon.domain.device.models.AppPackageName
import io.github.openflocon.domain.device.models.DeviceAppDomainModel
import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.device.models.HandleDeviceResultDomainModel
import io.github.openflocon.domain.device.models.RegisterDeviceWithAppDomainModel
import kotlinx.coroutines.flow.Flow

interface DevicesRepository {
    val devices: Flow<List<DeviceDomainModel>>
    val currentDeviceId: Flow<DeviceId?>
    val activeDevices: Flow<Set<DeviceIdAndPackageNameDomainModel>> // devices with active websocket connection

    suspend fun getCurrentDeviceId(): DeviceId?

    // returns new if new device
    suspend fun register(registerDeviceWithApp: RegisterDeviceWithAppDomainModel) : HandleDeviceResultDomainModel
    suspend fun getCurrentDevice(): DeviceDomainModel?
    suspend fun selectDevice(deviceId: DeviceId)

    // region apps
    fun observeDeviceApps(deviceId: DeviceId): Flow<List<DeviceAppDomainModel>>
    fun observeDeviceSelectedApp(deviceId: DeviceId): Flow<DeviceAppDomainModel?>
    suspend fun getDeviceSelectedApp(deviceId: DeviceId): DeviceAppDomainModel?
    suspend fun getDeviceAppByPackage(deviceId: DeviceId, appPackageName: String) : DeviceAppDomainModel?
    suspend fun selectApp(deviceId: DeviceId, app: DeviceAppDomainModel)
    // endregion

    // region app icon
    suspend fun saveAppIcon(deviceId: DeviceId, appPackageName: String, iconEncoded: String)
    suspend fun hasAppIcon(deviceId: DeviceId, appPackageName: String) : Boolean
    suspend fun askForDeviceAppIcon(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel)
// endregion

    fun observeDeviceSdkVersion(deviceId: DeviceId, appPackageName: String): Flow<String?>

    suspend fun deleteDevice(deviceId: DeviceId)
    suspend fun deleteApplication(
        deviceId : DeviceId,
        packageName : AppPackageName,
    )

    suspend fun restartApp(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel
    )

    suspend fun clear()
    fun observeCurrentDevice(): Flow<DeviceDomainModel?>
}

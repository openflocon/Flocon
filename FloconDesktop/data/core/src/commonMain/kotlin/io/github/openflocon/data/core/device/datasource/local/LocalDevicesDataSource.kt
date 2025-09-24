package io.github.openflocon.data.core.device.datasource.local

import io.github.openflocon.data.core.device.datasource.local.model.InsertResult
import io.github.openflocon.domain.device.models.AppPackageName
import io.github.openflocon.domain.device.models.DeviceAppDomainModel
import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
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
    suspend fun getDeviceAppByPackage(deviceId: DeviceId, packageName: AppPackageName) : DeviceAppDomainModel?
    fun observeDeviceAppByPackage(deviceId: DeviceId, packageName: AppPackageName) : Flow<DeviceAppDomainModel?>
    // endregion

    // region apps icons
    suspend fun saveAppIcon(deviceId: DeviceId, appPackageName: AppPackageName, iconEncoded: String)
    suspend fun hasAppIcon(deviceId: DeviceId, appPackageName: AppPackageName) : Boolean
    // endregion

    // fps
    suspend fun getIsDeviceDisplayingFps(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Boolean
    suspend fun saveIsDeviceDisplayingFps(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, value: Boolean)
    fun observeIsDeviceDisplayingFps(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<Boolean>
    // endregion

    suspend fun delete(deviceId: DeviceId)
    suspend fun deleteApp(deviceId: DeviceId, packageName: AppPackageName)

    suspend fun clear()
}


package io.github.openflocon.data.core.device.datasource.local

import io.github.openflocon.domain.device.models.AppInstance
import io.github.openflocon.domain.device.models.AppPackageName
import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface LocalCurrentDeviceDataSource {
    val currentDeviceId: Flow<DeviceId?>
    suspend fun getCurrentDeviceId(): DeviceId?
    suspend fun selectDevice(deviceId: DeviceId)

    suspend fun addNewDeviceConnectedForThisSession(deviceId: DeviceId)
    suspend fun isKnownDeviceForThisSession(deviceId: DeviceId): Boolean

    // region app
    fun observeDeviceSelectedApp(deviceId: DeviceId): Flow<AppPackageName?>
    suspend fun getDeviceSelectedApp(deviceId: DeviceId): AppPackageName?
    suspend fun selectApp(deviceId: DeviceId, packageName: AppPackageName)
    suspend fun isKnownAppForThisSession(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ) : Boolean
    suspend fun addNewDeviceAppConnectedForThisSession(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel
    )
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

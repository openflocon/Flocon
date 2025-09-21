package io.github.openflocon.data.local.device.datasource.local

import io.github.openflocon.data.core.device.datasource.local.LocalCurrentDeviceDataSource
import io.github.openflocon.domain.device.models.AppInstance
import io.github.openflocon.domain.device.models.AppPackageName
import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

// keep only in ram
class LocalCurrentDeviceDataSourceInMemory : LocalCurrentDeviceDataSource {

    private val _currentDeviceId = MutableStateFlow<DeviceId?>(null)
    override val currentDeviceId = _currentDeviceId.asStateFlow()

    private val connectedDevicesForSession = MutableStateFlow(emptySet<DeviceId>())
    private val connectedDevicesAndAppsForSession = MutableStateFlow(emptySet<DeviceIdAndPackageNameDomainModel>())
    private val currentDeviceApp = MutableStateFlow(emptyMap<DeviceId, AppPackageName>())
    private val isDeviceDisplayingFps = MutableStateFlow(emptyMap<DeviceIdAndPackageNameDomainModel, Boolean>())

    override suspend fun getCurrentDeviceId(): DeviceId? {
        return _currentDeviceId.value
    }

    override suspend fun selectDevice(deviceId: DeviceId) {
        _currentDeviceId.value = deviceId
    }

    override suspend fun selectApp(deviceId: DeviceId, packageName: AppPackageName) {
        currentDeviceApp.update {
            it + (deviceId to packageName)
        }
    }

    override suspend fun addNewDeviceConnectedForThisSession(deviceId: DeviceId) {
       connectedDevicesForSession.update { it + deviceId }
    }

    override suspend fun isKnownDeviceForThisSession(deviceId: DeviceId): Boolean {
       return connectedDevicesForSession.first().contains(deviceId)
    }

    override fun observeDeviceSelectedApp(deviceId: DeviceId): Flow<AppPackageName?> {
        return currentDeviceApp.map { it[deviceId] }
    }

    override suspend fun getDeviceSelectedApp(deviceId: DeviceId): AppPackageName? {
        return currentDeviceApp.first()[deviceId]
    }

    override suspend fun isKnownAppForThisSession(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Boolean {
        return connectedDevicesAndAppsForSession.first().contains(deviceIdAndPackageName)
    }

    override suspend fun addNewDeviceAppConnectedForThisSession(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ) {
        connectedDevicesAndAppsForSession.update { it + deviceIdAndPackageName }
    }

    override suspend fun getIsDeviceDisplayingFps(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Boolean {
        return isDeviceDisplayingFps.value[deviceIdAndPackageName] ?: false
    }

    override fun observeIsDeviceDisplayingFps(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<Boolean> {
        return isDeviceDisplayingFps.map { it[deviceIdAndPackageName] ?: false }
    }

    override suspend fun saveIsDeviceDisplayingFps(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, value: Boolean) {
        isDeviceDisplayingFps.update {
            it + (deviceIdAndPackageName to value)
        }
    }

    override suspend fun deleteApp(deviceId: DeviceId, packageName: AppPackageName) {
        connectedDevicesAndAppsForSession.update {
            it.filterNot {
                it.deviceId == deviceId && it.packageName == packageName
            }.toSet()
        }
        currentDeviceApp.update { map ->
            if(map[deviceId] == packageName)
                map - deviceId
            else map
        }
    }

    override suspend fun delete(deviceId: DeviceId) {
        _currentDeviceId.update {
            if(it == deviceId)
                null
            else it
        }
        connectedDevicesAndAppsForSession.update {
            it.filterNot {
                it.deviceId == deviceId
            }.toSet()
        }
        connectedDevicesAndAppsForSession.update {
            it.filterNot {
                it.deviceId == deviceId
            }.toSet()
        }
        currentDeviceApp.update {
            it.filterNot {
                it.key == deviceId
            }
        }
    }

    override suspend fun clear() {
        _currentDeviceId.update { null }
    }
}

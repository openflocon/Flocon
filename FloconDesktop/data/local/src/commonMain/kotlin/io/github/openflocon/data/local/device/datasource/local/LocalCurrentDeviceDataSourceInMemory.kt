package io.github.openflocon.data.local.device.datasource.local

import io.github.openflocon.data.core.device.datasource.local.LocalCurrentDeviceDataSource
import io.github.openflocon.domain.device.models.DeviceAppDomainModel
import io.github.openflocon.domain.device.models.DeviceId
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
    private val connectedDevicesAndAppsForSession = MutableStateFlow(emptySet<Pair<DeviceId,String>>())
    private val currentDeviceApp = MutableStateFlow(emptyMap<DeviceId, DeviceAppDomainModel>())

    override suspend fun getCurrentDeviceId(): DeviceId? {
        return _currentDeviceId.value
    }

    override suspend fun selectDevice(deviceId: DeviceId) {
        _currentDeviceId.value = deviceId
    }

    override suspend fun selectApp(deviceId: DeviceId, app: DeviceAppDomainModel) {
        currentDeviceApp.update {
            it + (deviceId to app)
        }
    }

    override suspend fun addNewDeviceConnectedForThisSession(deviceId: DeviceId) {
       connectedDevicesForSession.update { it + deviceId }
    }

    override suspend fun isKnownDeviceForThisSession(deviceId: DeviceId): Boolean {
       return connectedDevicesForSession.first().contains(deviceId)
    }

    override fun observeDeviceSelectedApp(deviceId: DeviceId): Flow<DeviceAppDomainModel?> {
        return currentDeviceApp.map { it[deviceId] }
    }

    override suspend fun getDeviceSelectedApp(deviceId: DeviceId): DeviceAppDomainModel? {
        return currentDeviceApp.first()[deviceId]
    }

    override suspend fun isKnownAppForThisSession(
        deviceId: DeviceId,
        packageName: String
    ): Boolean {
        val element = deviceId to packageName
        return connectedDevicesAndAppsForSession.first().contains(element)
    }

    override suspend fun addNewDeviceAppConnectedForThisSession(
        deviceId: DeviceId,
        packageName: String
    ) {
        val element = deviceId to packageName
        connectedDevicesAndAppsForSession.update { it + element }
    }

    override suspend fun clear() {
        _currentDeviceId.update { null }
    }
}

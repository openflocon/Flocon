package io.github.openflocon.data.local.device.datasource.local

import io.github.openflocon.data.core.device.datasource.local.LocalCurrentDeviceDataSource
import io.github.openflocon.domain.device.models.DeviceAppDomainModel
import io.github.openflocon.domain.device.models.DeviceId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update

// keep only in ram
class LocalCurrentDeviceDataSourceInMemory : LocalCurrentDeviceDataSource {

    private val _currentDeviceId = MutableStateFlow<DeviceId?>(null)
    override val currentDeviceId = _currentDeviceId.asStateFlow()

    private val _currentDeviceApp = MutableStateFlow<DeviceAppDomainModel?>(null)
    override val currentDeviceApp = _currentDeviceApp.asStateFlow()

    private val connectedDevicesForSession = MutableStateFlow(emptySet<DeviceId>())

    override suspend fun getCurrentDeviceId(): DeviceId? {
        return _currentDeviceId.value
    }

    override suspend fun getCurrentDeviceApp(): DeviceAppDomainModel? {
        return _currentDeviceApp.value
    }

    override suspend fun selectDevice(deviceId: DeviceId) {
        _currentDeviceId.value = deviceId
    }

    override suspend fun selectApp(app: DeviceAppDomainModel) {
        _currentDeviceApp.value = app
    }

    override suspend fun addNewDeviceConnectedForThisSession(deviceId: DeviceId) {
        connectedDevicesForSession.update { it + deviceId }
    }

    override suspend fun isKnownDeviceForThisSession(deviceId: DeviceId): Boolean {
        return connectedDevicesForSession.first().contains(deviceId)
    }

    override suspend fun clear() {
        _currentDeviceId.update { null }
        _currentDeviceApp.update { null }
        connectedDevicesForSession.update { emptySet() }
    }
}

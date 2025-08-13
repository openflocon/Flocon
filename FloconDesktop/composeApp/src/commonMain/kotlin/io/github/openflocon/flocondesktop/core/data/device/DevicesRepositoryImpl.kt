package io.github.openflocon.flocondesktop.core.data.device

import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.device.models.DeviceAppDomainModel
import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.repository.DevicesRepository
import io.github.openflocon.flocondesktop.common.Fakes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class DevicesRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
) : DevicesRepository {
    private val _devices = MutableStateFlow(defaultDevicesValue())
    override val devices = _devices.asStateFlow()

    private val _currentDevice = MutableStateFlow<DeviceDomainModel?>(defaultCurrentDeviceValue())
    override val currentDevice = _currentDevice.asStateFlow()

    private val _currentDeviceApp = MutableStateFlow<DeviceAppDomainModel?>(null)
    override val currentDeviceApp: Flow<DeviceAppDomainModel?> = _currentDeviceApp.asStateFlow()

    override fun getCurrentDevice(): DeviceDomainModel? = _currentDevice.value

    override fun getCurrentDeviceApp(): DeviceAppDomainModel? = _currentDeviceApp.value

    private val devicesMutex = Mutex()

    // returns new if new device
    override suspend fun register(device: DeviceDomainModel): Boolean = withContext(dispatcherProvider.data) {
        devicesMutex.withLock {
            val existingDevice = _devices.value.find { it.deviceId == device.deviceId }
            val isNewDevice = existingDevice == null

            _devices.update { currentDevices ->
                val updatedDevice = device.copy(
                    apps = device.apps.plus(existingDevice?.apps.orEmpty())
                        .distinctBy(DeviceAppDomainModel::packageName),
                )

                if (_currentDevice.value?.deviceId == device.deviceId) {
                    _currentDevice.update { updatedDevice }
                }

                val newDevicesList = if (isNewDevice) {
                    currentDevices + updatedDevice
                } else {
                    currentDevices.map {
                        if (it.deviceId == device.deviceId) updatedDevice else it
                    }
                }
                newDevicesList.distinct()
            }

            isNewDevice
        }
    }

    override suspend fun unregister(device: DeviceDomainModel) {
        withContext(dispatcherProvider.data) {
            devicesMutex.withLock {
                _devices.update { it - device }
            }
        }
    }

    override suspend fun clear() {
        withContext(dispatcherProvider.data) {
            devicesMutex.withLock {
                _devices.update { emptyList() }
            }
            _currentDevice.update { null }
            _currentDeviceApp.update { null }
        }
    }

    override suspend fun selectApp(app: DeviceAppDomainModel) {
        withContext(dispatcherProvider.data) {
            _currentDeviceApp.update { app }
        }
    }

    override suspend fun selectDevice(device: DeviceDomainModel) {
        withContext(dispatcherProvider.data) {
            _currentDevice.update { device }
        }
    }

    private fun defaultCurrentDeviceValue(): DeviceDomainModel = DeviceDomainModel(
        deviceId = Fakes.FakeDeviceId,
        deviceName = "deviceName",
        apps = listOf(
            DeviceAppDomainModel(
                name = "name",
                packageName = "com.package.name",
            ),
        ),
    )

    private fun defaultDevicesValue(): List<DeviceDomainModel> = if (Fakes.Enabled) {
        listOf(defaultCurrentDeviceValue())
    } else {
        emptyList()
    }
}

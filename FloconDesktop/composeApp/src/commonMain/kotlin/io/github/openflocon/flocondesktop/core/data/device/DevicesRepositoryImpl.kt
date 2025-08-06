package io.github.openflocon.flocondesktop.core.data.device

import io.github.openflocon.flocondesktop.common.Fakes
import io.github.openflocon.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import io.github.openflocon.flocondesktop.core.domain.device.repository.DevicesRepository
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceAppDomainModel
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    override suspend fun register(device: DeviceDomainModel) {
        withContext(dispatcherProvider.data) {
            _devices.update { (it + device).distinct() }
            // if no current device, select it
            _currentDevice.update {
                it ?: device
            }
        }
    }

    override suspend fun unregister(device: DeviceDomainModel) {
        withContext(dispatcherProvider.data) {
            _devices.update { it - device }
        }
    }

    override suspend fun clear() {
        withContext(dispatcherProvider.data) {
            _devices.update { emptyList() }
            _currentDevice.update { null }
        }
    }

    override suspend fun selectApp(app: DeviceAppDomainModel) {
        _currentDeviceApp.update { app }
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
                packageName = "com.package.name"
            )
        ),
    )

    private fun defaultDevicesValue(): List<DeviceDomainModel> = if (Fakes.Enabled) {
        listOf(defaultCurrentDeviceValue())
    } else {
        emptyList()
    }
}

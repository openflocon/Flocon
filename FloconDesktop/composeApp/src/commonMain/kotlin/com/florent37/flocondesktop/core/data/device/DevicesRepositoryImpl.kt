package com.florent37.flocondesktop.core.data.device

import com.florent37.flocondesktop.common.Fakes
import com.florent37.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import com.florent37.flocondesktop.core.domain.device.repository.DevicesRepository
import com.florent37.flocondesktop.messages.domain.model.DeviceDomainModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class DevicesRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
) : DevicesRepository {
    private val _devices = MutableStateFlow<List<DeviceDomainModel>>(defaultDevicesValue())
    override val devices = _devices.asStateFlow()

    private val _currentDevice = MutableStateFlow<DeviceDomainModel?>(defaultCurrentDeviceValue())
    override val currentDevice = _currentDevice.asStateFlow()

    override fun getCurrentDevice(): DeviceDomainModel? = _currentDevice.value

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

    override suspend fun selectDevice(device: DeviceDomainModel) {
        withContext(dispatcherProvider.data) {
            _currentDevice.update { device }
        }
    }

    private fun defaultCurrentDeviceValue(): DeviceDomainModel = DeviceDomainModel(
        appName = "appName",
        deviceName = "deviceName",
        appPackageName = "com.package.name",
        deviceId = Fakes.FakeDeviceId,
    )

    private fun defaultDevicesValue(): List<DeviceDomainModel> = if (Fakes.Enabled) {
        listOf(defaultCurrentDeviceValue())
    } else {
        emptyList()
    }
}

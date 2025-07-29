package com.florent37.flocondesktop.main.ui.delegates

import com.florent37.flocondesktop.common.coroutines.closeable.CloseableDelegate
import com.florent37.flocondesktop.common.coroutines.closeable.CloseableScoped
import com.florent37.flocondesktop.core.domain.device.ObserveCurrentDeviceUseCase
import com.florent37.flocondesktop.core.domain.device.ObserveDevicesUseCase
import com.florent37.flocondesktop.core.domain.device.SelectDeviceUseCase
import com.florent37.flocondesktop.main.ui.model.DeviceItemUiModel
import com.florent37.flocondesktop.main.ui.model.DevicesStateUiModel
import com.florent37.flocondesktop.messages.domain.model.DeviceDomainModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class DevicesDelegate(
    private val selectDeviceUseCase: SelectDeviceUseCase,
    private val observeDevicesUseCase: ObserveDevicesUseCase,
    private val observeCurrentDeviceUseCase: ObserveCurrentDeviceUseCase,
    private val closeableDelegate: CloseableDelegate,
) : CloseableScoped by closeableDelegate {

    val devicesState: StateFlow<DevicesStateUiModel> =
        combine(
            observeDevicesUseCase(),
            observeCurrentDeviceUseCase(),
        ) { devices, current ->
            if (devices.isEmpty()) {
                DevicesStateUiModel.Empty
            } else {
                if (current == null || current.deviceId !in devices.map { it.deviceId }) {
                    val firstDevice = devices.first()
                    selectDeviceUseCase(firstDevice.deviceId)
                    DevicesStateUiModel.WithDevices(
                        devices = mapToUi(devices),
                        selected = mapToUi(firstDevice),
                    )
                } else {
                    DevicesStateUiModel.WithDevices(
                        devices = mapToUi(devices),
                        selected = mapToUi(current),
                    )
                }
            }
        }.stateIn(
            scope = coroutineScope,
            SharingStarted.WhileSubscribed(5_000),
            DevicesStateUiModel.Loading,
        )

    suspend fun select(deviceId: String) {
        selectDeviceUseCase(deviceId)
    }

    private fun mapToUi(devices: List<DeviceDomainModel>): List<DeviceItemUiModel> = devices.map {
        mapToUi(it)
    }

    private fun mapToUi(device: DeviceDomainModel): DeviceItemUiModel = DeviceItemUiModel(
        appName = device.appName,
        deviceName = device.deviceName,
        appPackageName = device.appPackageName,
        id = device.deviceId,
    )
}

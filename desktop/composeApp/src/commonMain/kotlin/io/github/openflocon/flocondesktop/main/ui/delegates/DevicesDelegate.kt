package io.github.openflocon.flocondesktop.main.ui.delegates

import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableDelegate
import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableScoped
import io.github.openflocon.flocondesktop.core.domain.device.ObserveCurrentDeviceUseCase
import io.github.openflocon.flocondesktop.core.domain.device.ObserveDevicesUseCase
import io.github.openflocon.flocondesktop.core.domain.device.SelectDeviceUseCase
import io.github.openflocon.flocondesktop.main.ui.model.DeviceItemUiModel
import io.github.openflocon.flocondesktop.main.ui.model.DevicesStateUiModel
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceDomainModel
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

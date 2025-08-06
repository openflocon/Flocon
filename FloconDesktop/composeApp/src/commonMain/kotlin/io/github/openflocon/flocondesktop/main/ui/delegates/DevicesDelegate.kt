package io.github.openflocon.flocondesktop.main.ui.delegates

import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableDelegate
import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableScoped
import io.github.openflocon.flocondesktop.core.domain.device.ObserveCurrentDeviceAppUseCase
import io.github.openflocon.flocondesktop.core.domain.device.ObserveCurrentDeviceUseCase
import io.github.openflocon.flocondesktop.core.domain.device.ObserveDevicesUseCase
import io.github.openflocon.flocondesktop.core.domain.device.SelectDeviceUseCase
import io.github.openflocon.flocondesktop.main.ui.model.DeviceAppUiModel
import io.github.openflocon.flocondesktop.main.ui.model.DeviceItemUiModel
import io.github.openflocon.flocondesktop.main.ui.model.DevicesStateUiModel
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceAppDomainModel
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceDomainModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

class DevicesDelegate(
    private val selectDeviceUseCase: SelectDeviceUseCase,
    observeDevicesUseCase: ObserveDevicesUseCase,
    observeCurrentDeviceUseCase: ObserveCurrentDeviceUseCase,
    observeCurrentDeviceAppUseCase: ObserveCurrentDeviceAppUseCase,
    private val closeableDelegate: CloseableDelegate,
) : CloseableScoped by closeableDelegate {

    val devicesState: StateFlow<DevicesStateUiModel> =
        combine(
            observeDevicesUseCase(),
            observeCurrentDeviceUseCase(),
            observeCurrentDeviceAppUseCase()
        ) { devices, current, currentApp ->
            println("Devices: $devices")
            println("Device: $current")
            println("App: $currentApp")
            if (devices.isEmpty()) {
                DevicesStateUiModel.Empty
            } else {
                if (current == null || current.deviceId !in devices.map { it.deviceId }) {
                    val firstDevice = devices.first()
                    selectDeviceUseCase(firstDevice.deviceId)
                    DevicesStateUiModel.WithDevices(
                        devices = mapToUi(devices),
                        deviceSelected = mapToUi(firstDevice),
                        appSelected = currentApp?.let { mapToUi(it) }
                    )
                } else {
                    DevicesStateUiModel.WithDevices(
                        devices = mapToUi(devices),
                        deviceSelected = mapToUi(current),
                        appSelected = currentApp?.let { mapToUi(it) }
                    )
                }
            }
        }
            .onEach { println("DeviceState: $it") }
            .stateIn(
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
        deviceName = device.deviceName,
        id = device.deviceId,
        apps = device.apps
            .map { mapToUi(it) }
    )

    private fun mapToUi(app: DeviceAppDomainModel) = DeviceAppUiModel(
        name = app.name,
        packageName = app.packageName
    )
}

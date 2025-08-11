package io.github.openflocon.flocondesktop.main.ui.delegates

import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableDelegate
import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableScoped
import io.github.openflocon.flocondesktop.core.domain.device.ObserveCurrentDeviceAppUseCase
import io.github.openflocon.flocondesktop.core.domain.device.ObserveCurrentDeviceUseCase
import io.github.openflocon.flocondesktop.core.domain.device.ObserveDevicesUseCase
import io.github.openflocon.flocondesktop.core.domain.device.SelectDeviceAppUseCase
import io.github.openflocon.flocondesktop.core.domain.device.SelectDeviceUseCase
import io.github.openflocon.flocondesktop.main.ui.model.DeviceAppUiModel
import io.github.openflocon.flocondesktop.main.ui.model.DeviceItemUiModel
import io.github.openflocon.flocondesktop.main.ui.model.DevicesStateUiModel
import com.flocon.library.domain.models.DeviceAppDomainModel
import com.flocon.library.domain.models.DeviceDomainModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class DevicesDelegate(
    private val selectDeviceUseCase: SelectDeviceUseCase,
    private val selectDeviceAppUseCase: SelectDeviceAppUseCase,
    observeDevicesUseCase: ObserveDevicesUseCase,
    observeCurrentDeviceUseCase: ObserveCurrentDeviceUseCase,
    observeCurrentDeviceAppUseCase: ObserveCurrentDeviceAppUseCase,
    private val closeableDelegate: CloseableDelegate,
) : CloseableScoped by closeableDelegate {

    val devicesState: StateFlow<DevicesStateUiModel> =
        combine(
            observeDevicesUseCase(),
            observeCurrentDeviceUseCase(),
            observeCurrentDeviceAppUseCase(),
        ) { devices, current, currentApp ->
            if (devices.isEmpty()) {
                DevicesStateUiModel.Empty
            } else {
                if (current == null || current.deviceId !in devices.map { it.deviceId }) {
                    val firstDevice = devices.first()
                    select(firstDevice.deviceId)
                    DevicesStateUiModel.WithDevices(
                        devices = mapToUi(devices),
                        deviceSelected = mapToUi(firstDevice),
                        appSelected = defaultApp(firstDevice, currentApp),
                    )
                } else {
                    DevicesStateUiModel.WithDevices(
                        devices = mapToUi(devices),
                        deviceSelected = mapToUi(current),
                        appSelected = defaultApp(current, currentApp),
                    )
                }
            }
        }
            .stateIn(
                scope = coroutineScope,
                SharingStarted.WhileSubscribed(5_000),
                DevicesStateUiModel.Loading,
            )

    suspend fun select(deviceId: String) {
        selectDeviceUseCase(deviceId)
    }

    suspend fun selectApp(packageName: String) {
        selectDeviceAppUseCase(packageName)
    }

    private suspend fun defaultApp(device: DeviceDomainModel, currentApp: DeviceAppDomainModel?): DeviceAppUiModel? = if (currentApp == null || currentApp.packageName !in device.apps.map(DeviceAppDomainModel::packageName)) {
        device.apps
            .firstOrNull()
            ?.let {
                selectApp(it.packageName)
                mapToUi(it)
            }
    } else {
        mapToUi(currentApp)
    }

    private fun mapToUi(devices: List<DeviceDomainModel>): List<DeviceItemUiModel> = devices.map {
        mapToUi(it)
    }

    private fun mapToUi(device: DeviceDomainModel): DeviceItemUiModel = DeviceItemUiModel(
        deviceName = device.deviceName,
        id = device.deviceId,
        apps = device.apps
            .map { mapToUi(it) },
    )

    private fun mapToUi(app: DeviceAppDomainModel) = DeviceAppUiModel(
        name = app.name,
        packageName = app.packageName,
    )
}

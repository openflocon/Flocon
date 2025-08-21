package io.github.openflocon.flocondesktop.main.ui.delegates

import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceAppsUseCase
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdUseCase
import io.github.openflocon.domain.device.usecase.ObserveDevicesUseCase
import io.github.openflocon.domain.device.usecase.SelectDeviceAppUseCase
import io.github.openflocon.domain.device.usecase.SelectDeviceUseCase
import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableDelegate
import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableScoped
import io.github.openflocon.flocondesktop.main.ui.model.AppsStateUiModel
import io.github.openflocon.flocondesktop.main.ui.model.DevicesStateUiModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class DevicesDelegate(
    private val selectDeviceUseCase: SelectDeviceUseCase,
    private val selectDeviceAppUseCase: SelectDeviceAppUseCase,
    observeDevicesUseCase: ObserveDevicesUseCase,
    observeCurrentDeviceIdUseCase: ObserveCurrentDeviceIdUseCase,
    observeCurrentDeviceAppsUseCase: ObserveCurrentDeviceAppsUseCase,
    observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    private val closeableDelegate: CloseableDelegate,
) : CloseableScoped by closeableDelegate {

    val devicesState: StateFlow<DevicesStateUiModel> =
        combine(
            observeDevicesUseCase(),
            observeCurrentDeviceIdUseCase(),
        ) { devices, currentDeviceId ->
            if (devices.isEmpty()) {
                DevicesStateUiModel.Empty
            } else {
                val current = devices.firstOrNull { it.deviceId == currentDeviceId }
                if (current == null) {
                    val firstDevice = devices.first()
                    select(firstDevice.deviceId)
                    DevicesStateUiModel.WithDevices(
                        devices = mapToUi(devices),
                        deviceSelected = firstDevice.mapToUi(),
                    )
                } else {
                    DevicesStateUiModel.WithDevices(
                        devices = mapToUi(devices),
                        deviceSelected = current.mapToUi(),
                    )
                }
            }
        }.stateIn(
            scope = coroutineScope,
            SharingStarted.WhileSubscribed(5_000),
            DevicesStateUiModel.Loading,
        )

    val appsState: StateFlow<AppsStateUiModel> = combine(
        observeCurrentDeviceAppsUseCase(),
        observeCurrentDeviceIdAndPackageNameUseCase(),
    ) { apps, currentApp ->
        if (apps.isEmpty()) {
            AppsStateUiModel.Empty
        } else {
            val current = apps.firstOrNull { it.packageName == currentApp?.packageName }
            if (current == null) {
                val firstApp = apps.first()
                selectApp(firstApp.packageName)
                AppsStateUiModel.WithApps(
                    apps = mapAppsToUi(apps),
                    appSelected = null,
                )
            } else {
                AppsStateUiModel.WithApps(
                    apps = mapAppsToUi(apps),
                    appSelected = current.mapToUi(),
                )
            }
        }
    }
        .stateIn(
            scope = coroutineScope,
            SharingStarted.WhileSubscribed(5_000),
            AppsStateUiModel.Loading,
        )

    suspend fun select(deviceId: String) {
        selectDeviceUseCase(deviceId)
    }

    suspend fun selectApp(packageName: String) {
        selectDeviceAppUseCase(packageName)
    }
}

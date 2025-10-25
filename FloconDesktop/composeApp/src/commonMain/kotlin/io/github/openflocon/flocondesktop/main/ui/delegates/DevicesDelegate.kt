package io.github.openflocon.flocondesktop.main.ui.delegates

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.device.usecase.DeleteDeviceApplicationUseCase
import io.github.openflocon.domain.device.usecase.DeleteDeviceUseCase
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.device.usecase.ObserveActiveDevicesUseCase
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceAppsUseCase
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceCapabilitiesUseCase
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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DevicesDelegate(
    private val selectDeviceUseCase: SelectDeviceUseCase,
    private val selectDeviceAppUseCase: SelectDeviceAppUseCase,
    observeDevicesUseCase: ObserveDevicesUseCase,
    observeCurrentDeviceIdUseCase: ObserveCurrentDeviceIdUseCase,
    observeCurrentDeviceAppsUseCase: ObserveCurrentDeviceAppsUseCase,
    observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    observeActiveDevicesUseCase: ObserveActiveDevicesUseCase,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val deleteDeviceUseCase: DeleteDeviceUseCase,
    private val deleteDeviceApplicationUseCase: DeleteDeviceApplicationUseCase,
    private val closeableDelegate: CloseableDelegate,
    private val observeCurrentDeviceCapabilitiesUseCase: ObserveCurrentDeviceCapabilitiesUseCase,
) : CloseableScoped by closeableDelegate {

    val devicesState: StateFlow<DevicesStateUiModel> =
        combine(
            observeDevicesUseCase(),
            observeCurrentDeviceIdUseCase(),
            observeActiveDevicesUseCase(),
            observeCurrentDeviceCapabilitiesUseCase(),
        ) { devices, currentDeviceId, activeDevices, currentDeviceCapabilities ->
            if (devices.isEmpty()) {
                DevicesStateUiModel.Empty
            } else {
                val current = devices.firstOrNull { it.deviceId == currentDeviceId }
                if (current == null) {
                    val firstDevice = devices.first()
                    select(firstDevice.deviceId)
                    DevicesStateUiModel.WithDevices(
                        devices = mapListToUi(
                            devices = devices,
                            activeDevices = activeDevices
                        ),
                        deviceSelected = firstDevice.mapToUi(
                            activeDevices = activeDevices,
                            currentDeviceCapabilities = currentDeviceCapabilities,
                        ),
                    )
                } else {
                    DevicesStateUiModel.WithDevices(
                        devices = mapListToUi(
                            devices = devices,
                            activeDevices = activeDevices
                        ),
                        deviceSelected = current.mapToUi(
                            activeDevices = activeDevices,
                            currentDeviceCapabilities = currentDeviceCapabilities,
                        ),
                    )
                }
            }
        }.stateIn(
            scope = coroutineScope,
            SharingStarted.WhileSubscribed(5_000),
            DevicesStateUiModel.Loading,
        )

    init {
        // everytime we detect a new active app, we check if it's the current one, if not, we select it
        // do this only if we have 1 unique active device
        observeActiveDevicesUseCase().distinctUntilChanged().onEach { activeDevices ->
            val currentDeviceId = getCurrentDeviceIdAndPackageNameUseCase()?.deviceId
            if(activeDevices.size == 1 && currentDeviceId !in activeDevices.map { it.deviceId }) {
                val firstActiveDevice = activeDevices.first()
                select(firstActiveDevice.deviceId)
            }
        }.launchIn(coroutineScope)
    }

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

    suspend fun delete(deviceId: String) {
        deleteDeviceUseCase(deviceId)
    }

    suspend fun deleteApp(packageName: String) {
        // only fur the current device id
        val currentDeviceId = getCurrentDeviceIdAndPackageNameUseCase()?.deviceId ?: return
        deleteDeviceApplicationUseCase(
            deviceId = currentDeviceId,
            packageName = packageName,
        )
    }
}

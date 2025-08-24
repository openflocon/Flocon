package io.github.openflocon.flocondesktop.main.ui.model

import androidx.compose.runtime.Immutable

@Immutable
sealed interface DevicesStateUiModel {

    @Immutable
    data object Loading : DevicesStateUiModel

    @Immutable
    data object Empty : DevicesStateUiModel

    @Immutable
    data class WithDevices(
        val devices: List<DeviceItemUiModel>,
        val deviceSelected: DeviceItemUiModel,
    ) : DevicesStateUiModel
}

@Immutable
sealed interface AppsStateUiModel {

    val appSelected: DeviceAppUiModel?

    @Immutable
    data object Loading : AppsStateUiModel {
        override val appSelected: DeviceAppUiModel? = null
    }

    @Immutable
    data object Empty : AppsStateUiModel {
        override val appSelected: DeviceAppUiModel? = null
    }

    @Immutable
    data class WithApps(
        val apps: List<DeviceAppUiModel>,
        override val appSelected: DeviceAppUiModel?,
    ) : AppsStateUiModel
}

fun previewDevicesStateUiModel(): DevicesStateUiModel = DevicesStateUiModel.WithDevices(
    devices = listOf(
        DeviceItemUiModel(
            id = "id1",
            deviceName = "deviceName1",
            isActive = true,
        ),
        DeviceItemUiModel(
            id = "id2",
            deviceName = "deviceName2",
            isActive = true,
        ),
        DeviceItemUiModel(
            id = "id",
            deviceName = "deviceName",
            isActive = true,
        ),
    ),
    deviceSelected = DeviceItemUiModel(
        id = "id",
        deviceName = "deviceName",
        isActive = true,
    ),
)

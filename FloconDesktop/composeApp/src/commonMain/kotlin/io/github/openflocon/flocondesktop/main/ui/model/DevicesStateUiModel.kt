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
            deviceName = "deviceName1",
            id = "id1",
        ),
        DeviceItemUiModel(
            deviceName = "deviceName2",
            id = "id2",
        ),
        DeviceItemUiModel(
            deviceName = "deviceName",
            id = "id",
        ),
    ),
    deviceSelected = DeviceItemUiModel(
        deviceName = "deviceName",
        id = "id",
    ),
)

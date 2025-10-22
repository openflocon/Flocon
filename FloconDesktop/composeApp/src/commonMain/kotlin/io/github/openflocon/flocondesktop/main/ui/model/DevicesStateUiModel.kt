package io.github.openflocon.flocondesktop.main.ui.model

import androidx.compose.runtime.Immutable

@Immutable
sealed interface DevicesStateUiModel {

    val deviceSelected: DeviceItemUiModel?

    @Immutable
    data object Loading : DevicesStateUiModel {
        override val deviceSelected: DeviceItemUiModel? = null
    }

    @Immutable
    data object Empty : DevicesStateUiModel {
        override val deviceSelected: DeviceItemUiModel? = null
    }

    @Immutable
    data class WithDevices(
        val devices: List<DeviceItemUiModel>,
        override val deviceSelected: DeviceItemUiModel,
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
            platform = DeviceItemUiModel.Platform.Android,
        ),
        DeviceItemUiModel(
            id = "id2",
            deviceName = "deviceName2",
            isActive = true,
            platform = DeviceItemUiModel.Platform.Android,
        ),
        DeviceItemUiModel(
            id = "id",
            deviceName = "deviceName",
            isActive = true,
            platform = DeviceItemUiModel.Platform.Android,
        ),
    ),
    deviceSelected = DeviceItemUiModel(
        id = "id",
        deviceName = "deviceName",
        isActive = true,
        platform = DeviceItemUiModel.Platform.Android,
    ),
)

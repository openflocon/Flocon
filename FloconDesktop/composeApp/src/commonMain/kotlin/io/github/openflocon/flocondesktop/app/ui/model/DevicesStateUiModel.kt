package io.github.openflocon.flocondesktop.app.ui.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

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
    ) : DevicesStateUiModel {
        /** The devices grouped by platform, in the order Android, iOS, Desktop, others. */
        val groups: ImmutableList<DevicePlatformGroupUiModel> = PlatformsOrder.mapNotNull { platform ->
            devices.filter { it.platform == platform }
                .takeIf { it.isNotEmpty() }
                ?.let { DevicePlatformGroupUiModel(platform = platform, devices = it.toImmutableList()) }
        }.toImmutableList()
    }
}

@Immutable
data class DevicePlatformGroupUiModel(
    val platform: DeviceItemUiModel.Platform,
    val devices: ImmutableList<DeviceItemUiModel>,
)

private val PlatformsOrder = listOf(
    DeviceItemUiModel.Platform.Android,
    DeviceItemUiModel.Platform.ios,
    DeviceItemUiModel.Platform.Desktop,
    DeviceItemUiModel.Platform.Unknown,
)

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
            canScreenshot = true,
            canScreenRecord = true,
            canRestart = true,
        ),
        DeviceItemUiModel(
            id = "id2",
            deviceName = "deviceName2",
            isActive = true,
            platform = DeviceItemUiModel.Platform.Android,
            canScreenshot = true,
            canScreenRecord = true,
            canRestart = true,
        ),
        DeviceItemUiModel(
            id = "id",
            deviceName = "deviceName",
            isActive = true,
            platform = DeviceItemUiModel.Platform.Android,
            canScreenshot = true,
            canScreenRecord = true,
            canRestart = true,
        ),
    ),
    deviceSelected = DeviceItemUiModel(
        id = "id",
        deviceName = "deviceName",
        isActive = true,
        platform = DeviceItemUiModel.Platform.Android,
        canScreenshot = true,
        canScreenRecord = true,
        canRestart = true,
    ),
)

fun previewAppsStateUiModel(): AppsStateUiModel = AppsStateUiModel.WithApps(
    apps = listOf(
        DeviceAppUiModel(
            name = "appName1",
            packageName = "packageName",
            iconEncoded = null
        )
    ),
    appSelected = DeviceAppUiModel(
        name = "appName1",
        packageName = "packageName",
        iconEncoded = null
    )
)

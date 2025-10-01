package io.github.openflocon.flocondesktop.device

internal sealed interface DeviceAction {

    data class SelectTab(val selected: DeviceTab) : DeviceAction

    data object Refresh : DeviceAction

    data class ChangePermission(val permission: String, val granted: Boolean) : DeviceAction

}

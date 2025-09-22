package io.github.openflocon.flocondesktop.device

internal sealed interface DeviceAction {

    data class SelectTab(val index: Int) : DeviceAction

    data object Refresh : DeviceAction

    data class ChangePermission(val permission: String, val granted: Boolean) : DeviceAction

}

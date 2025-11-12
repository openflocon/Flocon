package io.github.openflocon.flocondesktop.device

internal sealed interface DeviceAction {

    data class SelectTab(val selected: DeviceTab) : DeviceAction

}

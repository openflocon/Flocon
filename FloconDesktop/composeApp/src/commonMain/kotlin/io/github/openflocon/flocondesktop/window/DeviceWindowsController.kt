package io.github.openflocon.flocondesktop.window

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

@Immutable
data class DeviceWindowUiModel(
    val deviceId: String,
    val title: String,
)

/**
 * The extra windows opened for a device, next to the main window.
 * Each one is pinned to its device, see [DeviceWindowScope].
 */
class DeviceWindowsController {

    val windows: StateFlow<List<DeviceWindowUiModel>>
        field = MutableStateFlow<List<DeviceWindowUiModel>>(emptyList())

    // one window by device: opening it again does nothing
    fun open(deviceId: String, title: String) {
        windows.update { current ->
            if (current.any { it.deviceId == deviceId }) {
                current
            } else {
                current + DeviceWindowUiModel(deviceId = deviceId, title = title)
            }
        }
    }

    fun close(deviceId: String) {
        windows.update { current -> current.filterNot { it.deviceId == deviceId } }
    }
}

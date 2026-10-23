package io.github.openflocon.flocondesktop.app.ui.settings

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class BluetoothUiState(
    /** The serial ports of the OS that look like a paired phone. */
    val ports: ImmutableList<String>,
    val status: BluetoothStatusUiModel,
    /** The port opened or being opened, null when [status] is [BluetoothStatusUiModel.Idle]. */
    val port: String?,
    val error: String?,
)

enum class BluetoothStatusUiModel { Idle, Connecting, Connected, Error }

fun previewBluetoothUiState() = BluetoothUiState(
    ports = persistentListOf("/dev/rfcomm0", "/dev/tty.Pixel7"),
    status = BluetoothStatusUiModel.Connected,
    port = "/dev/rfcomm0",
    error = null,
)

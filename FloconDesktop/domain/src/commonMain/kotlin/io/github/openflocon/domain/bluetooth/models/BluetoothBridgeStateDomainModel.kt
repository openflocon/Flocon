package io.github.openflocon.domain.bluetooth.models

/** The link to a phone over Bluetooth, see the Bluetooth bridge. */
data class BluetoothBridgeStateDomainModel(
    val status: Status,
    val port: String? = null,
    val error: String? = null,
) {
    enum class Status { Idle, Connecting, Connected, Error }

    companion object {
        val Idle = BluetoothBridgeStateDomainModel(status = Status.Idle)
    }
}

package io.github.openflocon.domain.bluetooth.repository

import io.github.openflocon.domain.bluetooth.models.BluetoothBridgeStateDomainModel
import kotlinx.coroutines.flow.StateFlow

interface BluetoothBridgeRepository {
    val state: StateFlow<BluetoothBridgeStateDomainModel>

    /** The serial ports of the OS that look like a paired phone. */
    fun listPorts(): List<String>

    /** Opens [port] and forwards what the phone sends to the server, until [disconnect] or the phone closes the link. */
    fun connect(port: String)

    fun disconnect()
}

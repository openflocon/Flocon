package io.github.openflocon.flocon

import io.github.openflocon.flocon.dsl.FloconMarker

/**
 * Bridge between [FloconBroadcastReceiver] (which must stay in this module, the desktop targets it by name)
 * and the device plugin, which lives in its own module.
 * The last received serial is kept, so a plugin observing after the broadcast still receives it.
 */
@FloconMarker
object FloconDeviceSerial {

    private val lock = Any()
    private var serial: String? = null
    private var listener: ((serial: String) -> Unit)? = null

    fun observe(onSerial: (serial: String) -> Unit) {
        val current = synchronized(lock) {
            listener = onSerial
            serial
        }
        current?.let(onSerial)
    }

    internal fun onSerialReceived(serial: String) {
        val current = synchronized(lock) {
            this.serial = serial
            listener
        }
        current?.invoke(serial)
    }
}

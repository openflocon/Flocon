package io.github.openflocon.flocon.myapplication.multi.ui

import io.github.openflocon.flocon.FloconConnectResult

/**
 * Scans the QR code of Flocon Desktop and connects to it, on the platforms that can: the Android app sets it.
 * The connection screen shows its button only when it is set.
 */
object SampleQrScanner {
    /** Returns the result of the connection, or `null` if the scan was cancelled. */
    var scan: (suspend () -> FloconConnectResult?)? = null
}

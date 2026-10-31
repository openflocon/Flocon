package io.github.openflocon.flocon.qrscanner

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconConnectResult
import io.github.openflocon.flocon.connect

/**
 * Connects to the desktop app at the address held by a scanned QR code.
 *
 * Same as `Flocon.connect(address)`, for the raw content of a QR code. The content is either the IP address
 * (or host) of the computer, or a URL whose host is that address, such as `flocon://192.168.1.20`: the scheme,
 * the port and the path are ignored. On Android, `Flocon.scanQrCodeAndConnect` scans the code and calls this.
 *
 * @param content the text read from the QR code.
 * @return [FloconConnectResult.Connected], or why it could not connect. The messages are kept
 * in every case, call it again to retry.
 */
suspend fun Flocon.Companion.connectWithQrCode(content: String): FloconConnectResult =
    connect(addressFromQrCode(content))

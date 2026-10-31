package io.github.openflocon.flocon.qrscanner

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconConnectResult

/** Does nothing, always [FloconConnectResult.NotStarted]. */
@Suppress("UNUSED_PARAMETER")
suspend fun Flocon.Companion.connectWithQrCode(content: String): FloconConnectResult = FloconConnectResult.NotStarted

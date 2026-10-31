package io.github.openflocon.flocon.qrscanner

import android.content.Context
import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconConnectResult

/** Does nothing, no scanner is opened: always `null`. */
@Suppress("UNUSED_PARAMETER")
suspend fun Flocon.Companion.scanQrCodeAndConnect(context: Context): FloconConnectResult? = null

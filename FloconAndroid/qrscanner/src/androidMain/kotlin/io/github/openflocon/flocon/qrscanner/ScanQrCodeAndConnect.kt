package io.github.openflocon.flocon.qrscanner

import android.content.Context
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconConnectResult
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Opens the Google code scanner, then connects to the desktop app at the address held by the QR code
 * it reads, see [connectWithQrCode].
 *
 * The scanner is a screen of Google Play Services: the app needs no camera permission. It requires
 * Google Play Services on the device, otherwise the result is [FloconConnectResult.Failed].
 *
 * ```
 * val result = Flocon.scanQrCodeAndConnect(context)
 * ```
 *
 * @param context preferably an `Activity`.
 * @return the result of the connection, or `null` if the user closed the scanner without scanning.
 */
suspend fun Flocon.Companion.scanQrCodeAndConnect(context: Context): FloconConnectResult? =
    when (val scan = scanQrCode(context)) {
        Scan.Cancelled -> null
        is Scan.Failed -> FloconConnectResult.Failed(scan.reason)
        is Scan.Code -> connectWithQrCode(scan.content)
    }

private sealed interface Scan {
    data class Code(val content: String) : Scan
    data object Cancelled : Scan
    data class Failed(val reason: String) : Scan
}

private suspend fun scanQrCode(context: Context): Scan = suspendCancellableCoroutine { continuation ->
    val options = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()
    GmsBarcodeScanning.getClient(context, options)
        .startScan()
        .addOnSuccessListener { barcode ->
            val content = barcode.rawValue
            continuation.resume(if (content == null) Scan.Failed("The QR code is empty") else Scan.Code(content))
        }
        .addOnCanceledListener { continuation.resume(Scan.Cancelled) }
        .addOnFailureListener { error -> continuation.resume(Scan.Failed(error.message ?: "The scanner failed")) }
}

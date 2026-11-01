package io.github.openflocon.flocon.qrscanner

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconConnectResult
import io.github.openflocon.flocon.connect
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Opens the Google code scanner, then connects to the desktop app at the address held by the QR code
 * it reads. The QR code of Flocon Desktop holds only the IP address of the computer, the same value as
 * `Flocon.connect(address)`.
 *
 * The scanner is a screen of Google Play Services: the app needs no camera permission. It requires
 * Google Play Services on the device, otherwise the result is [FloconConnectResult.Failed]: connect with
 * `Flocon.connect(address)` instead.
 *
 * ```
 * val result = Flocon.scanQrCodeAndConnect(context)
 * ```
 *
 * @param context preferably an `Activity`.
 * @return the result of the connection, or `null` if the user closed the scanner without scanning.
 */
suspend fun Flocon.Companion.scanQrCodeAndConnect(context: Context): FloconConnectResult? {
    if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) != ConnectionResult.SUCCESS) {
        return FloconConnectResult.Failed("Google Play Services is required to scan a QR code: connect with the IP address instead")
    }
    return when (val scan = scanQrCode(context)) {
        ScanQrCodeResult.Cancelled -> null
        is ScanQrCodeResult.Failed -> FloconConnectResult.Failed(scan.reason)
        is ScanQrCodeResult.Code -> connect(scan.content)
    }
}

private sealed interface ScanQrCodeResult {
    data class Code(val content: String) : ScanQrCodeResult
    data object Cancelled : ScanQrCodeResult
    data class Failed(val reason: String) : ScanQrCodeResult
}

private suspend fun scanQrCode(context: Context): ScanQrCodeResult =
    suspendCancellableCoroutine { continuation ->
        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        GmsBarcodeScanning.getClient(context, options)
            .startScan()
            .addOnSuccessListener { barcode ->
                val content = barcode.rawValue
                continuation.resume(
                    if (content == null) {
                        ScanQrCodeResult.Failed("The QR code is empty")
                    } else {
                        ScanQrCodeResult.Code(content)
                    }
                )
            }
            .addOnCanceledListener {
                continuation.resume(ScanQrCodeResult.Cancelled)
            }
            .addOnFailureListener { error ->
                continuation.resume(ScanQrCodeResult.Failed(error.message ?: "The scanner failed"))
            }
    }

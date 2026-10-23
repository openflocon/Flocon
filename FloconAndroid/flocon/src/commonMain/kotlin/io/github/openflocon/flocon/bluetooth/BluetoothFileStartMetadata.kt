package io.github.openflocon.flocon.bluetooth

import kotlinx.serialization.Serializable

/** The form fields of the http `/upload` request, sent in a [BluetoothFrame.FileStart]. */
@Serializable
internal data class BluetoothFileStartMetadata(
    val remotePath: String,
    val requestId: String,
    val deviceId: String,
    val appPackageName: String,
    val appInstance: Long,
    val fileName: String,
)

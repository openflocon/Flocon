package io.github.openflocon.flocondesktop

import kotlinx.serialization.Serializable

typealias DeviceId = String

@Serializable
data class FloconIncomingMessageDataModel(
    val deviceName: String,
    val deviceId: String,
    val plugin: String,
    val body: String,
    val method: String,
    val appName: String,
    val appPackageName: String,
)

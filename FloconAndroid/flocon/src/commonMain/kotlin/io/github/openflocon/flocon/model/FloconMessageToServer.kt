package io.github.openflocon.flocon.model

import kotlinx.serialization.Serializable

@Serializable
internal class FloconMessageToServer(
    val deviceName: String,
    val deviceId: String,
    val plugin: String,
    val method: String,
    val body: String,
    val appName: String,
    val appPackageName: String,
    val appInstance: Long, // app launch id / when the app has been launched
    val platform: String, // android, ios, desktop
    val versionName: String, // ex: 1.3.0
)

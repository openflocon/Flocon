package io.github.openflocon.flocon.model

import io.github.openflocon.flocon.core.FloconEncoder
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

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
)

internal fun FloconMessageToServer.toFloconMessageToServer(): String {
    return FloconEncoder.json.encodeToString(this)
}

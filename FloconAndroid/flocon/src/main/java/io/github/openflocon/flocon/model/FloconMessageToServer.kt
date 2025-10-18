package io.github.openflocon.flocon.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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
)

internal fun FloconMessageToServer.toFloconMessageToServer(
    json: Json,
): String {
    return json.encodeToString(this)
}

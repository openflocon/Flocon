package io.github.openflocon.flocon.model

import org.json.JSONObject

internal fun toFloconMessageToServer(
    deviceName: String,
    deviceId: String,
    plugin: String,
    method: String,
    body: String,
    appName: String,
    appPackageName: String,
    appInstance: Long, // app launch id / when the app has been launched
): String {
    val json = JSONObject()

    json.put("deviceName", deviceName)
    json.put("deviceId", deviceId)
    json.put("plugin", plugin)
    json.put("method", method)
    json.put("body", body)
    json.put("appName", appName)
    json.put("appPackageName", appPackageName)
    json.put("appInstance", appInstance)

    return json.toString()
}

package io.github.openflocon.flocondesktop

import io.github.openflocon.flocondesktop.core.data.settings.datasource.local.createSettings
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class WindowStateData(
    val width: Int,
    val height: Int,
    val x: Int,
    val y: Int,
)

object WindowStateSaver {

    val settings = createSettings()

    fun save(state: WindowStateData) {
        try {
            settings.putString("window", Json.encodeToString<WindowStateData>(state))
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    fun load(): WindowStateData? {
        return try {
            settings.getStringOrNull("window")?.let { json ->
                Json.decodeFromString<WindowStateData>(json)
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            null
        }
    }
}

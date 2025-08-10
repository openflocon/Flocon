package io.github.openflocon.flocondesktop.window

import io.github.openflocon.flocondesktop.core.data.settings.datasource.local.createSettings
import kotlinx.serialization.json.Json

object WindowStateSaver {

    val settings = createSettings()

    fun save(state: WindowStateData) {
        try {
            settings.putString("window", Json.encodeToString<WindowStateData>(state))
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    fun load(): WindowStateData? = try {
        settings.getStringOrNull("window")?.let { json ->
            Json.decodeFromString<WindowStateData>(json)
        }
    } catch (t: Throwable) {
        t.printStackTrace()
        null
    }
}

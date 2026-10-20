package io.github.openflocon.flocondesktop.telemetry

import com.russhwolf.settings.Settings
import io.github.openflocon.flocondesktop.core.data.settings.datasource.local.createSettings

/** Persists the user's telemetry choice and, only in [TelemetryLevel.Full], the installation id. */
interface TelemetryPreferences {
    /** `null` until the user answered the consent prompt. */
    var level: TelemetryLevel?

    var installationId: String?
}

internal class TelemetryPreferencesImpl(
    private val settings: Settings = createSettings(),
) : TelemetryPreferences {

    override var level: TelemetryLevel?
        get() = settings.getStringOrNull(LEVEL)
            ?.let { name -> TelemetryLevel.entries.firstOrNull { it.name == name } }
        set(value) {
            if (value == null) settings.remove(LEVEL) else settings.putString(LEVEL, value.name)
        }

    override var installationId: String?
        get() = settings.getStringOrNull(INSTALLATION_ID)
        set(value) {
            if (value == null) settings.remove(INSTALLATION_ID) else settings.putString(INSTALLATION_ID, value)
        }

    private companion object {
        const val LEVEL = "telemetry_level"
        const val INSTALLATION_ID = "telemetry_installation_id"
    }
}

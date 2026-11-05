package io.github.openflocon.flocondesktop.core.data.settings

import com.russhwolf.settings.Settings
import io.github.openflocon.domain.models.settings.ServerPortsDomainModel
import io.github.openflocon.domain.models.settings.ServerPortsOverrides
import io.github.openflocon.flocondesktop.core.data.settings.datasource.local.SettingsDataSourcePrefs
import io.github.openflocon.flocondesktop.core.data.settings.datasource.local.createSettings

/**
 * The server ports given as launch options, for the current session only.
 * Set once in main(), before Koin starts. Never persisted.
 */
object LaunchServerPorts {
    @Volatile
    var overrides: ServerPortsOverrides = ServerPortsOverrides.None

    /** The ports saved in the settings, read synchronously (before Koin starts). */
    fun readSavedPorts(settings: Settings = createSettings()): ServerPortsDomainModel = SettingsDataSourcePrefs.parseServerPorts(settings.getStringOrNull(SettingsDataSourcePrefs.SERVER_PORTS))
}

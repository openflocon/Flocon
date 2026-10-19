package io.github.openflocon.flocon.plugins.sharedprefs

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.plugins.sharedprefs.model.FloconPreference

// no local preferences detected on this platform, use FloconPreferencesPlugin.register
internal actual fun buildFloconPreferencesDataSource(context: FloconContext): FloconPreferencesDataSource =
    object : FloconPreferencesDataSource {
        override fun detectLocalPreferences(): List<FloconPreference> = emptyList()
    }

package io.github.openflocon.flocon.plugins.sharedprefs

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.plugins.sharedprefs.model.FloconPreference

internal actual fun buildFloconPreferencesDataSource(context: FloconContext): FloconPreferencesDataSource {
    return FloconPreferencesDataSourceJvm()
}

internal class FloconPreferencesDataSourceJvm : FloconPreferencesDataSource {
    override fun detectLocalPreferences(): List<FloconPreference> {
        return emptyList()
    }
}
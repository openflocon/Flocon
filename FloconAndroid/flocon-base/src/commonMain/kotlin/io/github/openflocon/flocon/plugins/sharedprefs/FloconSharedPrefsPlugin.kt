package io.github.openflocon.flocon.plugins.sharedprefs

import io.github.openflocon.flocon.*
import io.github.openflocon.flocon.plugins.sharedprefs.model.FloconSharedPreferenceModel

class FloconPreferencesConfig

/**
 * Flocon Preferences Plugin.
 * Used to inspect SharedPreferences or other key-value stores.
 */
expect object FloconPreferences : FloconPluginFactory<FloconPreferencesConfig, FloconPreferencesPlugin>

fun floconRegisterSharedPreference(sharedPreference: FloconSharedPreferenceModel) {
    FloconApp.instance?.client?.preferencesPlugin?.register(sharedPreference)
}

interface FloconPreferencesPlugin : FloconPlugin {
    fun register(sharedPreference: FloconSharedPreferenceModel)
}
package io.github.openflocon.flocon.pluginsold.sharedprefs

import io.github.openflocon.flocon.*
import io.github.openflocon.flocon.pluginsold.sharedprefs.model.FloconSharedPreferenceModel

class FloconPreferencesConfig

/**
 * Flocon Preferences Plugin.
 * Used to inspect SharedPreferences or other key-value stores.
 */
object FloconPreferences : FloconPluginFactory<FloconPreferencesConfig, FloconPreferencesPlugin> {
    override fun createConfig(): FloconPreferencesConfig {
        TODO("Not yet implemented")
    }

    override fun install(config: Any, app: FloconApp): FloconPreferencesPlugin {
        TODO("Not yet implemented")
    }

    override val name: String
        get() = TODO("Not yet implemented")
}

//fun floconRegisterSharedPreference(sharedPreference: FloconSharedPreferenceModel) {
//    FloconApp.instance?.client?.preferencesPlugin?.register(sharedPreference)
//}

interface FloconPreferencesPlugin : FloconPlugin {
    fun register(sharedPreference: FloconSharedPreferenceModel)
}
package io.github.openflocon.flocon.pluginsold.sharedprefs

import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.pluginsold.sharedprefs.model.FloconSharedPreferenceModel

class FloconPreferencesConfig : FloconPluginConfig

/**
 * Flocon Preferences Plugin.
 * Used to inspect SharedPreferences or other key-value stores.
 */
object FloconPreferences : FloconPluginFactory<FloconPreferencesConfig, FloconPreferencesPlugin> {
    override fun createConfig(context: FloconContext): FloconPreferencesConfig {
        TODO("Not yet implemented")
    }

    override fun install(
        pluginConfig: FloconPreferencesConfig,
        floconConfig: FloconConfig
    ): FloconPreferencesPlugin {
        TODO("Not yet implemented")
    }

    override val name: String
        get() = TODO("Not yet implemented")
    override val pluginId: String
        get() = TODO("Not yet implemented")
}

//fun floconRegisterSharedPreference(sharedPreference: FloconSharedPreferenceModel) {
//    FloconApp.instance?.client?.preferencesPlugin?.register(sharedPreference)
//}

interface FloconPreferencesPlugin : FloconPlugin {
    fun register(sharedPreference: FloconSharedPreferenceModel)
}
package io.github.openflocon.flocon.pluginsold.sharedprefs

import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.pluginsold.sharedprefs.model.FloconSharedPreferenceModel

class FloconPreferencesConfig : FloconPluginConfig

object FloconPreferences : FloconPluginFactory<FloconPreferencesConfig, FloconPreferencesPlugin> {
    override val name: String = "Preferences"
    override val pluginId: String = "preferences"
    override fun createConfig(context: FloconContext): FloconPreferencesConfig {
        return FloconPreferencesConfig()
    }

    override fun install(
        pluginConfig: FloconPreferencesConfig,
        floconConfig: FloconConfig,
        encoder: FloconEncoder
    ): FloconPreferencesPlugin {
        return FloconSharedPrefsPluginNoOpImpl()
    }
}

interface FloconPreferencesPlugin : FloconPlugin {
    fun register(sharedPreference: FloconSharedPreferenceModel)
}

internal class FloconSharedPrefsPluginNoOpImpl : FloconPlugin, FloconPreferencesPlugin {
    override val key: String = "SHARED_PREF"

    override suspend fun onMessageReceived(
        method: String,
        body: String,
    ) {
        // no op
    }

    override suspend fun onConnectedToServer() {
        // no op
    }

    override fun register(sharedPreference: FloconSharedPreferenceModel) {
        // no op
    }
}

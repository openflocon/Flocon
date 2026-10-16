package io.github.openflocon.flocon.plugins.sharedprefs

import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.plugins.sharedprefs.model.FloconSharedPreferenceModel

class FloconPreferencesConfig : FloconPluginConfig

interface FloconPreferencesPlugin : FloconPlugin {
    fun register(sharedPreference: FloconSharedPreferenceModel)
}

object FloconPreferences : FloconPluginFactory<FloconPreferencesConfig, FloconPreferencesPlugin> {
    override val name: String = "Preferences"
    override val pluginId: String = Protocol.ToDevice.SharedPreferences.Plugin
    override fun createConfig(context: FloconContext) = FloconPreferencesConfig()
    override fun install(
        pluginConfig: FloconPreferencesConfig,
        floconConfig: FloconConfig,
        encoder: FloconEncoder
    ): FloconPreferencesPlugin {
        return FloconSharedPrefsPluginNoOpImpl()
    }
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

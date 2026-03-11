package io.github.openflocon.flocon.plugins.deeplinks

import io.github.openflocon.flocon.*

actual object FloconDeeplinks : FloconPluginFactory<FloconDeeplinksConfig, FloconDeeplinksPlugin> {
    override val name: String = "Deeplinks"
    override val pluginId: String = null
    override fun createConfig() = FloconDeeplinksConfig()
    override fun install(config: FloconDeeplinksConfig, app: FloconApp): FloconDeeplinksPlugin {
        return FloconDeeplinksPluginNoOp
    }
}

private object FloconDeeplinksPluginNoOp : FloconDeeplinksPlugin {
    override fun registerDeeplinks(deeplinks: List<DeeplinkModel>) {
        // no-op
    }

    override fun onMessageReceived(method: String, body: String) {
        // no-op
    }

    override fun onConnectedToServer() {
        // no-op
    }
}

fun floconRegisterDeeplink(vararg deeplinks: String) {
    // no-op
}

fun floconRegisterDeeplinks(deeplinks: List<DeeplinkModel>) {
    // no-op
}

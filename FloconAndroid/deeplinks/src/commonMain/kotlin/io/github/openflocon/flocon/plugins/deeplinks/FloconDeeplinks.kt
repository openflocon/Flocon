package io.github.openflocon.flocon.plugins.deeplinks

import io.github.openflocon.flocon.*
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.plugins.deeplinks.model.DeeplinkModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

object FloconDeeplinks : FloconPluginFactory<FloconDeeplinksConfig, FloconDeeplinksPlugin> {
    override val name: String = "Deeplinks"
    override val pluginId: String = FloconDeeplinks::class.simpleName!!
    override fun createConfig() = FloconDeeplinksConfig()
    override fun install(config: FloconDeeplinksConfig, app: FloconApp): FloconDeeplinksPlugin {
        val plugin = FloconDeeplinksPluginImpl(
            sender = app.client as FloconMessageSender
        )
        if (config.deeplinks.isNotEmpty()) {
            plugin.registerDeeplinks(config.deeplinks)
        }
        return plugin
    }
}

internal class FloconDeeplinksPluginImpl(
    private val sender: FloconMessageSender,
) : FloconPlugin, FloconDeeplinksPlugin {
    override val key: String = "DEEP_LINK"

    private val deeplinks = MutableStateFlow<List<DeeplinkModel>?>(null)

    override fun onMessageReceived(
        method: String,
        body: String,
    ) {
        // no op
    }

    override fun onConnectedToServer() {
        // on connected, send known deeplinks
        deeplinks.value?.let {
            registerDeeplinks(it)
        }
    }

    override fun registerDeeplinks(deeplinks: List<DeeplinkModel>) {
        this.deeplinks.update {
            deeplinks
        }

        try {
            sender.send(
                plugin = Protocol.FromDevice.Deeplink.Plugin,
                method = Protocol.FromDevice.Deeplink.Method.GetDeeplinks,
                body = toDeeplinksJson(deeplinks)
            )
        } catch (t: Throwable) {
            FloconLogger.logError("deeplink mapping error", t)
        }
    }
}

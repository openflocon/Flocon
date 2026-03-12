package io.github.openflocon.flocon.plugins.deeplinks

import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.plugins.deeplinks.model.DeeplinkModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

object FloconDeeplinks : FloconPluginFactory<FloconDeeplinksConfig, FloconDeeplinksPlugin> {
    override val name: String = "Deeplinks"
    override val pluginId: String = FloconDeeplinks::class.simpleName!!
    override fun createConfig() = FloconDeeplinksConfig()
    override fun install(config: Any, app: FloconApp): FloconDeeplinksPlugin {
        println("Deeplinks: install ($config)")
        val config = config as FloconDeeplinksConfig // TODO
        val plugin = FloconDeeplinksPluginImpl(
            sender = app.client as FloconMessageSender
        )
        if (config.deeplinks.isNotEmpty()) {
            plugin.registerDeeplinks(config.deeplinks)
        }
        println("Deeplinks: $plugin")
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
        println("Deeplinks: message received $($method) ($body)")
        // no op
    }

    override fun onConnectedToServer() {
        println("Deeplinks: connected (${deeplinks.value})")
        // on connected, send known deeplinks
        deeplinks.value?.let {
            registerDeeplinks(it)
        }
    }

    override fun registerDeeplinks(deeplinks: List<DeeplinkModel>) {
        this.deeplinks.update { deeplinks }

        try {
            println("Deeplinks: sending")
            sender.send(
                plugin = Protocol.FromDevice.Deeplink.Plugin,
                method = Protocol.FromDevice.Deeplink.Method.GetDeeplinks,
                body = toDeeplinksJson(deeplinks)
            )
            println("Deeplinks: sent")
        } catch (t: Throwable) {
            println("Deeplinks: error: ${t.message}")
            t.printStackTrace()
            FloconLogger.logError("deeplink mapping error", t)
        }
    }
}

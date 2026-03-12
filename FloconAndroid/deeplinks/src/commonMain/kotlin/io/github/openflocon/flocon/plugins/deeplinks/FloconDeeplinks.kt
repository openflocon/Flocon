package io.github.openflocon.flocon.plugins.deeplinks

import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.dsl.FloconMarker
import io.github.openflocon.flocon.plugins.deeplinks.model.DeeplinkModel

object FloconDeeplinks : FloconPluginFactory<FloconDeeplinksConfig, FloconDeeplinksPlugin> {
    override val name: String = "Deeplinks"
    override val pluginId: String = FloconDeeplinks::class.simpleName!!
    override fun createConfig() = FloconDeeplinksConfig()

    @OptIn(FloconMarker::class)
    override fun install(
        pluginConfig: FloconDeeplinksConfig,
        floconConfig: FloconConfig
    ): FloconDeeplinksPlugin {
        val plugin = FloconDeeplinksPluginImpl(
            deeplinks = pluginConfig.deeplinks,
            sender = floconConfig.client as FloconMessageSender
        )

        return plugin
    }
}

internal class FloconDeeplinksPluginImpl(
    private val deeplinks: List<DeeplinkModel>,
    private val sender: FloconMessageSender,
) : FloconPlugin, FloconDeeplinksPlugin {
    override val key: String = "DEEP_LINK"

    override suspend fun onMessageReceived(
        method: String,
        body: String,
    ) {
        // no op
    }

    override suspend fun onConnectedToServer() {
        registerDeeplinks(deeplinks)
    }

    override suspend fun registerDeeplinks(deeplinks: List<DeeplinkModel>) {
        try {
            sender.send(
                plugin = Protocol.FromDevice.Deeplink.Plugin,
                method = Protocol.FromDevice.Deeplink.Method.GetDeeplinks,
                body = toDeeplinksJson(deeplinks)
            )
        } catch (t: Throwable) {
            t.printStackTrace()
            FloconLogger.logError("deeplink mapping error", t)
        }
    }
}

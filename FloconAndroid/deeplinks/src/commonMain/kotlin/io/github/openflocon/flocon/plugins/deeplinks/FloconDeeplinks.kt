package io.github.openflocon.flocon.plugins.deeplinks

import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconEncoding
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.core.encode
import io.github.openflocon.flocon.dsl.FloconMarker
import io.github.openflocon.flocon.plugins.deeplinks.model.DeeplinkModel

object FloconDeeplinks : FloconPluginFactory<FloconDeeplinksConfig, FloconDeeplinksPlugin> {
    override val name: String = "Deeplinks"
    override val pluginId: String = FloconDeeplinks::class.simpleName!!
    override fun createConfig(context: FloconContext): FloconDeeplinksConfig =
        FloconDeeplinksConfigImpl()

    @FloconMarker
    override fun createEncoding(): FloconEncoding = FloconDeeplinkEncoding()

    @OptIn(FloconMarker::class)
    override fun install(
        pluginConfig: FloconDeeplinksConfig,
        floconConfig: FloconConfig,
        encoder: FloconEncoder
    ): FloconDeeplinksPlugin {
        val plugin = FloconDeeplinksPluginImpl(
            deeplinks = pluginConfig.deeplinks(),
            variables = pluginConfig.variables(),
            sender = floconConfig.client as FloconMessageSender,
            encoder = encoder
        )

        return plugin
    }
}

internal class FloconDeeplinksPluginImpl(
    private val deeplinks: List<DeeplinkModel>,
    private val variables: List<DeeplinkVariable>,
    private val sender: FloconMessageSender,
    private val encoder: FloconEncoder
) : FloconPlugin, FloconDeeplinksPlugin {
    override val key: String = "DEEP_LINK"

    override suspend fun onMessageReceived(
        method: String,
        body: String,
    ) {
        // no op
    }

    override suspend fun onConnectedToServer() {
        registerDeeplinks(
            deeplinks = deeplinks,
            variables = variables
        )
    }

    fun registerDeeplinks(
        deeplinks: List<DeeplinkModel>,
        variables: List<DeeplinkVariable>
    ) {
        try {
            sender.send(
                plugin = Protocol.FromDevice.Deeplink.Plugin,
                method = Protocol.FromDevice.Deeplink.Method.GetDeeplinks,
                body = encoder.encode(createRemote(deeplinks = deeplinks, variables = variables))
            )
        } catch (t: Throwable) {
            t.printStackTrace()
            FloconLogger.logError("deeplink mapping error", t)
        }
    }
}

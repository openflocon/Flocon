package io.github.openflocon.flocon.deeplinks

import io.github.openflocon.flocon.Flocon
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
import io.github.openflocon.flocon.deeplinks.model.DeeplinkModel
import io.github.openflocon.flocon.error.pluginNotInitialized
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

object FloconDeeplinks : FloconPluginFactory<FloconDeeplinksConfig, FloconDeeplinksPlugin> {
    override val name: String = "Deeplinks"
    override val pluginId: String = Protocol.FromDevice.Deeplink.Plugin
    override fun createConfig(context: FloconContext): FloconDeeplinksConfig =
        FloconDeeplinksConfigImpl()

    override fun createEncoding(): FloconEncoding = FloconDeeplinkEncoding()

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
            .also { FloconDeeplinksPluginImpl.plugin = it }

        return plugin
    }
}

internal class FloconDeeplinksPluginImpl(
    deeplinks: List<DeeplinkModel>,
    variables: List<DeeplinkVariable>,
    private val sender: FloconMessageSender,
    private val encoder: FloconEncoder,
    dispatcher: CoroutineDispatcher = Dispatchers.Default.limitedParallelism(1),
) : FloconPlugin, FloconDeeplinksPlugin {
    override val key: String = Protocol.FromDevice.Deeplink.Plugin

    // only touched from [scope], which runs on a single thread
    private val scope = CoroutineScope(dispatcher + SupervisorJob())
    private var deeplinks: List<DeeplinkModel> = deeplinks
    private var variables: List<DeeplinkVariable> = variables

    override fun register(block: FloconDeeplinksConfig.() -> Unit) {
        val config = FloconDeeplinksConfigImpl().apply(block)
        scope.launch {
            val newDeeplinks = config.deeplinks()
            val newVariables = config.variables()
            deeplinks = deeplinks.filterNot { old -> newDeeplinks.any { it.link == old.link } } + newDeeplinks
            variables = variables.filterNot { old -> newVariables.any { it.name == old.name } } + newVariables
            registerDeeplinks(deeplinks = deeplinks, variables = variables)
        }
    }

    override suspend fun onMessageReceived(
        method: String,
        body: String,
    ) {
        // no op
    }

    override suspend fun onConnectedToServer() {
        scope.launch {
            registerDeeplinks(
                deeplinks = deeplinks,
                variables = variables
            )
        }.join()
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

    companion object {
        var plugin: FloconDeeplinksPlugin? = null
    }
}

val Flocon.Companion.deeplinksPlugin: FloconDeeplinksPlugin
    get() = FloconDeeplinksPluginImpl.plugin ?: pluginNotInitialized("Deeplinks", NoOpDeeplinksPlugin())

private class NoOpDeeplinksPlugin : FloconDeeplinksPlugin {
    override val key: String = ""
    override suspend fun onMessageReceived(method: String, body: String) = Unit
    override suspend fun onConnectedToServer() = Unit
    override fun register(block: FloconDeeplinksConfig.() -> Unit) = Unit
}

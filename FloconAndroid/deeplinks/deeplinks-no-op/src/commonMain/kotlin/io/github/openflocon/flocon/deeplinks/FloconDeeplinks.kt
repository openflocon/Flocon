package io.github.openflocon.flocon.deeplinks

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.core.FloconEncoder

class DeeplinkLinkBuilder internal constructor(private val link: String) {
    var label: String? = null
    var description: String? = null

    infix fun String.withAutoComplete(suggestions: List<String>) {}
    infix fun String.withVariable(variableName: String) {}
}

class DeeplinkVariableBuilder internal constructor(private val name: String) {
    var description: String? = null
    fun autoComplete(suggestions: List<String>) {}
}

abstract class FloconDeeplinksConfig : FloconPluginConfig {
    abstract fun variable(name: String, block: DeeplinkVariableBuilder.() -> Unit = {})
    abstract fun deeplink(link: String, block: DeeplinkLinkBuilder.() -> Unit = {})
}

internal class FloconDeeplinksConfigImpl : FloconDeeplinksConfig() {
    override fun variable(name: String, block: DeeplinkVariableBuilder.() -> Unit) {}
    override fun deeplink(link: String, block: DeeplinkLinkBuilder.() -> Unit) {}
}

interface FloconDeeplinksPlugin : FloconPlugin {
    fun register(block: FloconDeeplinksConfig.() -> Unit)
}

object FloconDeeplinks : FloconPluginFactory<FloconDeeplinksConfig, FloconDeeplinksPlugin> {
    override val name: String = "Deeplinks"
    override val pluginId: String = "deeplink"

    override fun createConfig(context: FloconContext): FloconDeeplinksConfig = FloconDeeplinksConfigImpl()

    override fun install(
        pluginConfig: FloconDeeplinksConfig,
        floconConfig: FloconConfig,
        encoder: FloconEncoder
    ): FloconDeeplinksPlugin {
        return FloconDeeplinksPluginNoOp
    }
}

internal object FloconDeeplinksPluginNoOp : FloconDeeplinksPlugin {
    override val key: String = "deeplink"
    override suspend fun onMessageReceived(method: String, body: String) {}
    override suspend fun onConnectedToServer() {}
    override fun register(block: FloconDeeplinksConfig.() -> Unit) {}
}

val Flocon.Companion.deeplinksPlugin: FloconDeeplinksPlugin
    get() = FloconDeeplinksPluginNoOp

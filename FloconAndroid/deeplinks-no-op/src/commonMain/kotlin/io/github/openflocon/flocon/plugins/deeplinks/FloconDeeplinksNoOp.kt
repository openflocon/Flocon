package io.github.openflocon.flocon.deeplinks

import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.core.FloconEncoder

class DeeplinkModel

class DeeplinkVariable

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
    internal abstract fun deeplinks(): List<DeeplinkModel>
    internal abstract fun variables(): List<DeeplinkVariable>
}

internal class FloconDeeplinksConfigImpl : FloconDeeplinksConfig() {
    override fun variable(name: String, block: DeeplinkVariableBuilder.() -> Unit) {}
    override fun deeplink(link: String, block: DeeplinkLinkBuilder.() -> Unit) {}
    override fun deeplinks(): List<DeeplinkModel> = emptyList()
    override fun variables(): List<DeeplinkVariable> = emptyList()
}

interface FloconDeeplinksPlugin : FloconPlugin

object FloconDeeplinks : FloconPluginFactory<FloconDeeplinksConfig, FloconDeeplinksPlugin> {
    override val name: String = "Deeplinks"
    override val pluginId: String = "FloconDeeplinks"

    override fun createConfig(context: FloconContext): FloconDeeplinksConfig = FloconDeeplinksConfigImpl()

    override fun install(
        pluginConfig: FloconDeeplinksConfig,
        floconConfig: FloconConfig,
        encoder: FloconEncoder
    ): FloconDeeplinksPlugin {
        return FloconDeeplinksPluginNoOp
    }
}

private object FloconDeeplinksPluginNoOp : FloconDeeplinksPlugin {
    override val key: String = "DEEP_LINK"
    override suspend fun onMessageReceived(method: String, body: String) {}
    override suspend fun onConnectedToServer() {}
}

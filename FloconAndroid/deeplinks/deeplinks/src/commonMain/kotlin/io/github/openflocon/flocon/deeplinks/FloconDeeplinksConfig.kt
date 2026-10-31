package io.github.openflocon.flocon.deeplinks

import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.deeplinks.model.DeeplinkModel

abstract class FloconDeeplinksConfig : FloconPluginConfig {
    abstract fun variable(name: String, block: DeeplinkVariableBuilder.() -> Unit = {})

    abstract fun deeplink(link: String, block: DeeplinkLinkBuilder.() -> Unit = {})

    internal abstract fun deeplinks(): List<DeeplinkModel>
    internal abstract  fun variables(): List<DeeplinkVariable>
}

internal class FloconDeeplinksConfigImpl internal constructor() : FloconDeeplinksConfig() {

    private val variables = mutableListOf<DeeplinkVariable>()
    private val deeplinks = mutableListOf<DeeplinkModel>()

    override fun variable(name: String, block: DeeplinkVariableBuilder.() -> Unit) {
        val variable = DeeplinkVariableBuilder(name).apply(block)
            .build()

        variables.add(variable)
    }

    override fun deeplink(link: String, block: DeeplinkLinkBuilder.() -> Unit) {
        val deeplink = DeeplinkLinkBuilder(link).apply(block)
            .build()

        deeplinks.add(deeplink)
    }

    override fun deeplinks(): List<DeeplinkModel> = deeplinks.toList()
    override fun variables(): List<DeeplinkVariable> = variables.toList()

}
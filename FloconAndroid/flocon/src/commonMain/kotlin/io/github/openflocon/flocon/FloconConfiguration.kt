package io.github.openflocon.flocon

import io.github.openflocon.flocon.client.FloconClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob

class FloconConfiguration internal constructor(
    private val config: FloconConfig
) {

    private val plugins: MutableMap<String, (FloconConfig) -> FloconPlugin> = mutableMapOf()

    /**
     * Install a plugin with the given [factory] and optional [configure] block.
     */
    fun <Config : FloconPluginConfig, Plugin : FloconPlugin> install(
        factory: FloconPluginFactory<Config, Plugin>,
        configure: Config.() -> Unit = {}
    ) {
        plugins[factory.pluginId] = { scope ->
            val config = factory.createConfig(config.context)
                .apply { configure() }

            factory.install(
                pluginConfig = config,
                floconConfig = scope
            )
        }
    }

    fun build(): List<FloconPlugin> {
        return plugins.values.map { it.invoke(config) }
    }

}

@ConsistentCopyVisibility
data class FloconConfig internal constructor(
    val context: FloconContext,
    val scope: CoroutineScope,
    val client: FloconClient
)

fun startFlocon(context: FloconContext, block: FloconConfiguration.() -> Unit) {
    val config = FloconConfig(
        context = context,
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
        client = FloconClient(context = context)
    )
    val configuration = FloconConfiguration(config = config)
        .apply(block)

    Flocon(
        config = config,
        plugins = configuration.build()
    )
}
package io.github.openflocon.flocon

import io.github.openflocon.flocon.client.FloconClientImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FloconConfiguration internal constructor() {

    internal val pluginConfigs = mutableMapOf<FloconPluginFactory<Any, *>, Any>()

    /**
     * Install a plugin with the given [factory] and optional [configure] block.
     */
    fun <Config : Any, PluginInstance : Any> install(
        factory: FloconPluginFactory<Config, PluginInstance>,
        configure: Config.() -> Unit = {}
    ) {
        val config = factory.createConfig()
        config.configure()
        pluginConfigs[factory as FloconPluginFactory<Any, *>] = config // TODO
    }

}

fun startFlocon(context: FloconContext, block: FloconConfiguration.() -> Unit) {
    val configuration = FloconConfiguration().apply(block)

    Flocon(
        context = context,
        plugins = configuration.pluginConfigs.map { (factory, config) ->
            factory.install(
                config,
                DumpObject(
                    FloconClientImpl(context, configuration, plugins = emptyList())
                )
            ) as FloconPlugin
        }
    )
}

class DumpObject(
    client: Client
) : FloconApp() {

    override val client: Client = client

    private val _initialized = MutableStateFlow<Boolean>(false)
    override val isInitialized: StateFlow<Boolean> = _initialized.asStateFlow()

}
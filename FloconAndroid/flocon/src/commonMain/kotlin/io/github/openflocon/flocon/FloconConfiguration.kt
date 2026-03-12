package io.github.openflocon.flocon

import io.github.openflocon.flocon.client.FloconClient
import io.github.openflocon.flocon.client.FloconClientImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
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
    val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    val client = FloconClient(context = context, scope = scope)

    Flocon(
        context = context,
        scope = scope,
        client = client,
        plugins = configuration.pluginConfigs.map { (factory, config) ->
            factory.install(
                config = config,
                app = DumpObject(client) // TODO Change
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
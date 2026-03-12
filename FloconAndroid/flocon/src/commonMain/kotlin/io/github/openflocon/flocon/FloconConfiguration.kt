package io.github.openflocon.flocon

import io.github.openflocon.flocon.client.FloconClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FloconConfiguration internal constructor(
    private val client: FloconClient
) {

    internal val plugins = mutableListOf<FloconPlugin>()

    /**
     * Install a plugin with the given [factory] and optional [configure] block.
     */
    fun <Config : FloconPluginConfig, Plugin : FloconPlugin> install(
        factory: FloconPluginFactory<Config, Plugin>,
        configure: Config.() -> Unit = {}
    ) {
        val plugin = factory.install(
            config = factory.createConfig()
                .apply { configure() },
            app = DumpObject(client = client)
        )

        plugins.add(plugin)
    }

}

fun startFlocon(context: FloconContext, block: FloconConfiguration.() -> Unit) {
    val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    val client = FloconClient(context = context)
    val configuration = FloconConfiguration(client = client).apply(block)

    Flocon(
        context = context,
        scope = scope,
        client = client,
        plugins = configuration.plugins
    )
}

class DumpObject(
    client: Client
) : FloconApp() {

    override val client: Client = client

    private val _initialized = MutableStateFlow<Boolean>(false)
    override val isInitialized: StateFlow<Boolean> = _initialized.asStateFlow()

}
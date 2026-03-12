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
    private val context: FloconContext,
    private val client: FloconClient
) {

    private val plugins: MutableMap<String, (FloconApp) -> FloconPlugin> = mutableMapOf()

    /**
     * Install a plugin with the given [factory] and optional [configure] block.
     */
    fun <Config : FloconPluginConfig, Plugin : FloconPlugin> install(
        factory: FloconPluginFactory<Config, Plugin>,
        configure: Config.() -> Unit = {}
    ) {
        plugins[factory.pluginId] = { scope ->
            val config = factory.createConfig()
                .apply { configure() }

            factory.install(
                config = config,
                app = scope
            )
        }
    }

    fun build(): List<FloconPlugin> {
        val app = DumpObject(
            context = context,
            client = client
        )

        return plugins.values.map { it.invoke(app) }
    }

}

fun startFlocon(context: FloconContext, block: FloconConfiguration.() -> Unit) {
    val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    val client = FloconClient(context = context)
    val configuration = FloconConfiguration(
        context = context,
        client = client
    )
        .apply(block)

    Flocon(
        context = context,
        scope = scope,
        client = client,
        plugins = configuration.build()
    )
}

class DumpObject(
    context: FloconContext,
    client: Client
) : FloconApp() {

    override val client: Client = client

    private val _initialized = MutableStateFlow<Boolean>(false)
    override val isInitialized: StateFlow<Boolean> = _initialized.asStateFlow()

    init {
        this.context = context
        instance = this
    }

}
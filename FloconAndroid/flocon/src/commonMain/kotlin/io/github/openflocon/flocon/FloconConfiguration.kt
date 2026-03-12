package io.github.openflocon.flocon

import io.github.openflocon.flocon.client.FloconClient
import io.github.openflocon.flocon.dsl.FloconMarker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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
            val config = factory.createConfig()
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
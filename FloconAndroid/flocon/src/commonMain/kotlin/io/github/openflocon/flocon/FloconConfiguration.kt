package io.github.openflocon.flocon

import io.github.openflocon.flocon.client.FloconClient
import io.github.openflocon.flocon.core.FloconEncoder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus

class FloconConfiguration internal constructor(
    private val config: FloconConfig
) {

    private val plugins: MutableMap<String, (FloconConfig, FloconEncoder) -> FloconPlugin> = mutableMapOf()

    private var serializerModule = SerializersModule {}

    /**
     * Install a plugin with the given [factory] and optional [configure] block.
     */
    fun <Config : FloconPluginConfig, Plugin : FloconPlugin> install(
        factory: FloconPluginFactory<Config, Plugin>,
        configure: Config.() -> Unit = {}
    ) {
        plugins[factory.pluginId] = { scope, encoder ->
            val config = factory.createConfig(config.context)
                .apply { configure() }

            factory.install(
                pluginConfig = config,
                floconConfig = scope,
                encoder = encoder
            )
        }

        serializerModule += factory.createEncoding().serializersModule
    }

    fun build(encoder: FloconEncoder): List<FloconPlugin> {
        return plugins.values.map { it.invoke(config, encoder) }
    }

    fun encoding() = serializerModule

}

@ConsistentCopyVisibility
data class FloconConfig internal constructor(
    val context: FloconContext,
    val scope: CoroutineScope,
    val client: FloconClient
)

fun startFlocon(
    context: FloconContext,
    block: FloconConfiguration.() -> Unit
) {
    val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    val client = FloconClient(
        context = context,
        scope = scope
    )
    val config = FloconConfig(
        context = context,
        scope = scope,
        client = client
    )
    val configuration = FloconConfiguration(
        config = config,
    )
        .apply(block)
    val encoder = FloconEncoder(module = configuration.encoding())

    client.setupEncoder(encoder)

    Flocon(
        config = config,
        plugins = configuration.build(encoder),
        encoder = encoder
    )
}
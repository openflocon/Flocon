package io.github.openflocon.flocon

/**
 * Configuration builder for Flocon SDK.
 * Used in [Flocon.initialize] to configure the SDK and install plugins.
 */
class FloconConfiguration internal constructor() {
    internal val pluginConfigs = mutableMapOf<FloconPluginFactory<*, *>, Any>()

    /**
     * Install a plugin with the given [factory] and optional [configure] block.
     */
    fun <Config : Any, PluginInstance : Any> install(
        factory: FloconPluginFactory<Config, PluginInstance>,
        configure: Config.() -> Unit = {}
    ) {
        val config = factory.createConfig()
        config.configure()
        pluginConfigs[factory] = config
    }
}

fun flocon(block: FloconConfiguration.() -> Unit) {
    val configuration = FloconConfiguration().apply(block)


}
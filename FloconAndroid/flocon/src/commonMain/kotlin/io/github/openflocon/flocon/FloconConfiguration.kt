package io.github.openflocon.flocon

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

fun startFlocon(context: FloconContext, block: FloconConfiguration.() -> Unit) {
    val configuration = FloconConfiguration().apply(block)
    
    
}
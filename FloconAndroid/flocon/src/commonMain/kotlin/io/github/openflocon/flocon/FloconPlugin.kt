package io.github.openflocon.flocon

/**
 * Base interface for all Flocon plugins.
 * Plugins can receive messages from the server and react to connection events.
 */
interface FloconPlugin {
    val key: String

    suspend fun onMessageReceived(
        method: String,
        body: String,
    )

    suspend fun onConnectedToServer()
}

interface FloconPluginConfig

/**
 * A unique key for identifying a Flocon plugin.
 */
interface FloconPluginKey<Config : Any, PluginInstance : Any> {
    val name: String
    val pluginId: String
}

/**
 * A factory for creating and installing Flocon plugins.
 * This is the entry point for Ktor-style [install] calls.
 */
interface FloconPluginFactory<Config : FloconPluginConfig, PluginInstance : FloconPlugin> : FloconPluginKey<Config, PluginInstance> {

    /**
     * Create a default configuration instance for the plugin.
     */
    fun createConfig(): Config

    /**
     * Install the plugin into the [io.github.openflocon.flocon.FloconApp] instance with the given [config].
     */
    fun install(config: Config, app: FloconApp): PluginInstance // TODO

}

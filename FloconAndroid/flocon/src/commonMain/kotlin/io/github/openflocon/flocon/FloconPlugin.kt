package io.github.openflocon.flocon

/**
 * Base interface for all Flocon plugins.
 * Plugins can receive messages from the server and react to connection events.
 */
interface FloconPlugin {
    val key: String

    fun onMessageReceived(
        method: String,
        body: String,
    )

    fun onConnectedToServer()
}

/**
 * A unique key for identifying a Flocon plugin.
 */
interface FloconPluginKey<Config : Any, PluginInstance : Any> {
    val name: String
    val pluginId: String? get() = null
}

/**
 * A factory for creating and installing Flocon plugins.
 * This is the entry point for Ktor-style [install] calls.
 */
interface FloconPluginFactory<Config : Any, PluginInstance : Any> : FloconPluginKey<Config, PluginInstance> {
    /**
     * Create a default configuration instance for the plugin.
     */
    fun createConfig(): Config

    /**
     * Install the plugin into the [io.github.openflocon.flocon.FloconApp] instance with the given [config].
     */
    fun install(config: Any, app: FloconApp): PluginInstance // TODO
}

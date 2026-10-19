package io.github.openflocon.flocon

import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.dsl.FloconMarker
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

interface FloconPlugin {
    val key: String

    suspend fun onMessageReceived(
        method: String,
        body: String,
    )

    suspend fun onConnectedToServer()
}

interface FloconPluginConfig

interface FloconPluginKey<Config : Any, PluginInstance : Any> {
    val name: String
    val pluginId: String
}

interface FloconPluginFactory<Config : FloconPluginConfig, PluginInstance : FloconPlugin> :
    FloconPluginKey<Config, PluginInstance> {

    fun createEncoding(): FloconEncoding = object : FloconEncoding {
        override val serializersModule: SerializersModule = EmptySerializersModule()
    }

    fun createConfig(context: FloconContext): Config

    @OptIn(FloconMarker::class)
    fun install(
        pluginConfig: Config,
        floconConfig: FloconConfig,
        encoder: FloconEncoder
    ): PluginInstance
}

interface FloconEncoding {
    val serializersModule: SerializersModule
}

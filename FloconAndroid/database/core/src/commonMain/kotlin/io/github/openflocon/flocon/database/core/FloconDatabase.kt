package io.github.openflocon.flocon.database.core

import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconEncoding
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.dsl.FloconMarker

object FloconDatabase : FloconPluginFactory<FloconDatabaseConfig, FloconDatabasePlugin> {
    override val name: String = "Database"
    override val pluginId: String = Protocol.ToDevice.Database.Plugin

    @FloconMarker
    override fun createEncoding(): FloconEncoding = FloconDatabaseEncoding()

    override fun createConfig() = FloconDatabaseConfig()

    @OptIn(FloconMarker::class)
    override fun install(
        pluginConfig: FloconDatabaseConfig,
        floconConfig: FloconConfig
    ): FloconDatabasePlugin {
        return FloconDatabasePluginImpl(
            sender = floconConfig.client as FloconMessageSender,
            context = floconConfig.context,
            providers = pluginConfig.providers
        ).also { FloconDatabasePluginImpl.plugin = it }
    }

}

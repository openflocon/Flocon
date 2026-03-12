package io.github.openflocon.flocon.pluginsold.database

import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.pluginsold.database.model.FloconDatabaseModel

class FloconDatabaseConfig : FloconPluginConfig

/**
 * Flocon Database Plugin.
 * Used to inspect Room or other SQL databases.
 */
object FloconDatabase : FloconPluginFactory<FloconDatabaseConfig, FloconDatabasePlugin> {
    override fun createConfig(): FloconDatabaseConfig {
        TODO("Not yet implemented")
    }

    override fun install(config: FloconDatabaseConfig, app: FloconApp): FloconDatabasePlugin {
        TODO("Not yet implemented")
    }

    override val name: String
        get() = TODO("Not yet implemented")
    override val pluginId: String
        get() = TODO("Not yet implemented")
}


interface FloconDatabasePlugin : FloconPlugin {
    fun register(floconDatabaseModel: FloconDatabaseModel)
    fun logQuery(dbName: String, sqlQuery: String, bindArgs: List<Any?>)
}
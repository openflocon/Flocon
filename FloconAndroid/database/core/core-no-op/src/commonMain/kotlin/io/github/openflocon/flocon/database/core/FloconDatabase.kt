package io.github.openflocon.flocon.database.core

import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.database.core.datasource.FloconDatabaseProvider

object FloconDatabase : FloconPluginFactory<FloconDatabaseConfig, FloconDatabasePlugin> {
    override val name: String = "Database"
    override val pluginId: String = "database"

    override fun createConfig(context: FloconContext): FloconDatabaseConfig =
        FloconDatabaseConfig(context)

    override fun install(
        pluginConfig: FloconDatabaseConfig,
        floconConfig: FloconConfig,
        encoder: FloconEncoder
    ): FloconDatabasePlugin = FloconDatabasePluginNoOp
}

class FloconDatabaseConfig(
    val context: FloconContext
) : FloconPluginConfig {

    val providers: MutableList<FloconDatabaseProvider> = mutableListOf()

}

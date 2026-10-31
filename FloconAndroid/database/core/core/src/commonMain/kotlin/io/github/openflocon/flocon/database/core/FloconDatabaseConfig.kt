package io.github.openflocon.flocon.database.core

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.database.core.datasource.FloconDatabaseProvider

class FloconDatabaseConfig(
    val context: FloconContext
) : FloconPluginConfig {

    val providers: MutableList<FloconDatabaseProvider> = mutableListOf()

}

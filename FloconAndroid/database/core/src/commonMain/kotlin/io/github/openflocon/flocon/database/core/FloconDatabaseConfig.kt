package io.github.openflocon.flocon.database.core

import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.database.core.datasource.FloconDatabaseProvider
import io.github.openflocon.flocon.dsl.FloconMarker

class FloconDatabaseConfig : FloconPluginConfig {

    @FloconMarker
    val providers: MutableList<FloconDatabaseProvider> = mutableListOf()

}

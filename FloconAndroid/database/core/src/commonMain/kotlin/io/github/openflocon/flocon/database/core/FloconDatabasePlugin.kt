package io.github.openflocon.flocon.database.core

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.database.core.datasource.FloconDatabaseProvider
import io.github.openflocon.flocon.database.core.model.FloconDatabaseModel
import io.github.openflocon.flocon.dsl.FloconMarker
import io.github.openflocon.flocon.error.pluginNotInitialized

interface FloconDatabasePlugin : FloconPlugin {

    @FloconMarker
    val providers: List<FloconDatabaseProvider>

    fun register(floconDatabaseModel: FloconDatabaseModel)

    fun logQuery(dbName: String, sqlQuery: String, bindArgs: List<Any?>)

}

@OptIn(FloconMarker::class)
val Flocon.Companion.databasePlugin: FloconDatabasePlugin
    get() = FloconDatabasePluginImpl.plugin ?: pluginNotInitialized("Database")
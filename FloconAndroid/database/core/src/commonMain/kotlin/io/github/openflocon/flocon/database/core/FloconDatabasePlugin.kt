package io.github.openflocon.flocon.database.core

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.database.core.datasource.FloconDatabaseProvider
import io.github.openflocon.flocon.database.core.model.FloconDatabaseModel
import io.github.openflocon.flocon.error.pluginNotInitialized

interface FloconDatabasePlugin : FloconPlugin {

    val providers: List<FloconDatabaseProvider>

    fun register(floconDatabaseModel: FloconDatabaseModel)

    fun logQuery(dbName: String, sqlQuery: String, bindArgs: List<Any?>)

}

val Flocon.Companion.databasePlugin: FloconDatabasePlugin
    get() = FloconDatabasePluginImpl.plugin ?: pluginNotInitialized("Database", NoOpDatabasePlugin())

private class NoOpDatabasePlugin : FloconDatabasePlugin {
    override val key: String = ""
    override val providers: List<FloconDatabaseProvider> = emptyList()
    override fun register(floconDatabaseModel: FloconDatabaseModel) = Unit
    override fun logQuery(dbName: String, sqlQuery: String, bindArgs: List<Any?>) = Unit
    override suspend fun onMessageReceived(method: String, body: String) = Unit
    override suspend fun onConnectedToServer() = Unit
}
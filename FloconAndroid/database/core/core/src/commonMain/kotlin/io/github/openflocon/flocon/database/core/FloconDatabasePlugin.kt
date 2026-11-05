package io.github.openflocon.flocon.database.core

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.database.core.datasource.FloconDatabaseProvider
import io.github.openflocon.flocon.database.core.model.FloconDatabaseModel
import io.github.openflocon.flocon.error.pluginNotInitialized

interface FloconDatabasePlugin : FloconPlugin {

    val providers: List<FloconDatabaseProvider>

    /**
     * @throws IllegalArgumentException if another database is already registered with the same [FloconDatabaseModel.id]
     */
    fun register(floconDatabaseModel: FloconDatabaseModel)

    /**
     * Logs a query, the plugin finds the transaction it belongs to from the queries already logged for [dbId]
     * (BEGIN, COMMIT, SAVEPOINT...), so all the queries of a transaction are grouped in the desktop app.
     * Not thread safe for a given [dbId]: call it from one thread at a time.
     *
     * @param dbId the id the database was registered with (see [register]), the queries are shown with this database in the desktop app
     */
    fun logQuery(dbId: String, sqlQuery: String, bindArgs: List<Any?>)

    /**
     * Logs a query whose transaction is already known by the caller, ex: a driver that follows each connection.
     *
     * @param transactionId the same id for all the queries of a transaction (BEGIN and COMMIT included). Null outside of a transaction
     */
    fun logQuery(dbId: String, sqlQuery: String, bindArgs: List<Any?>, transactionId: String?)

}

val Flocon.Companion.databasePlugin: FloconDatabasePlugin
    get() = FloconDatabasePluginImpl.plugin ?: pluginNotInitialized("Database", NoOpDatabasePlugin())

private class NoOpDatabasePlugin : FloconDatabasePlugin {
    override val key: String = ""
    override val providers: List<FloconDatabaseProvider> = emptyList()
    override fun register(floconDatabaseModel: FloconDatabaseModel) = Unit
    override fun logQuery(dbId: String, sqlQuery: String, bindArgs: List<Any?>) = Unit
    override fun logQuery(dbId: String, sqlQuery: String, bindArgs: List<Any?>, transactionId: String?) = Unit
    override suspend fun onMessageReceived(method: String, body: String) = Unit
    override suspend fun onConnectedToServer() = Unit
}
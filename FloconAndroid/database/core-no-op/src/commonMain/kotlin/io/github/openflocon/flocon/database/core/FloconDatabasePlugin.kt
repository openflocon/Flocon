package io.github.openflocon.flocon.database.core

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.database.core.datasource.FloconDatabaseProvider
import io.github.openflocon.flocon.database.core.model.FloconDatabaseModel

interface FloconDatabasePlugin : FloconPlugin {

    val providers: List<FloconDatabaseProvider>

    fun register(floconDatabaseModel: FloconDatabaseModel)

    fun logQuery(dbId: String, sqlQuery: String, bindArgs: List<Any?>)

}

internal object FloconDatabasePluginNoOp : FloconDatabasePlugin {
    override val key: String = "database"
    override val providers: List<FloconDatabaseProvider> = emptyList()
    override fun register(floconDatabaseModel: FloconDatabaseModel) = Unit
    override fun logQuery(dbId: String, sqlQuery: String, bindArgs: List<Any?>) = Unit
    override suspend fun onMessageReceived(method: String, body: String) = Unit
    override suspend fun onConnectedToServer() = Unit
}

val Flocon.Companion.databasePlugin: FloconDatabasePlugin
    get() = FloconDatabasePluginNoOp

package io.github.openflocon.flocon.database.room3

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.database.core.databasePlugin

internal fun interface QueryLogger {
    fun log(dbId: String, sql: String, bindArgs: List<Any?>)
}

internal object DatabasePluginQueryLogger : QueryLogger {
    override fun log(dbId: String, sql: String, bindArgs: List<Any?>) {
        Flocon.databasePlugin.logQuery(dbId = dbId, sqlQuery = sql, bindArgs = bindArgs)
    }
}

package io.github.openflocon.flocon.plugins.database

import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.plugins.database.model.FloconDatabaseModel
import io.github.openflocon.flocon.plugins.database.model.FloconFileDatabaseModel

fun floconRegisterDatabase(database: FloconDatabaseModel) {
    FloconApp.instance?.client?.databasePlugin?.register(
        database
    )
}

fun floconRegisterDatabase(displayName: String, absolutePath: String) {
    floconRegisterDatabase(
        FloconFileDatabaseModel(
            displayName = displayName,
            absolutePath = absolutePath,
        )
    )
}

fun floconLogDatabaseQuery(dbName: String, sqlQuery: String, bindArgs: List<Any?>) {
    FloconApp.instance?.client?.databasePlugin?.logQuery(
        dbName = dbName,
        sqlQuery = sqlQuery,
        bindArgs = bindArgs,
    )
}

interface FloconDatabasePlugin {
    fun register(floconDatabaseModel: FloconDatabaseModel)
    fun logQuery(dbName: String, sqlQuery: String, bindArgs: List<Any?>)
}
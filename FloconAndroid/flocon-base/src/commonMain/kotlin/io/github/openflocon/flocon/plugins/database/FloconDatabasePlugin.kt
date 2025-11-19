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

interface FloconDatabasePlugin {
    fun register(floconDatabaseModel: FloconDatabaseModel)
}
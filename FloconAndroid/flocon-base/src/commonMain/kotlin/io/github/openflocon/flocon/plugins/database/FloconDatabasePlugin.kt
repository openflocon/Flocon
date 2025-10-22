package io.github.openflocon.flocon.plugins.database

import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.plugins.database.model.FloconDatabaseModel

fun floconRegisterDatabase(displayName: String, absolutePath: String) {
    FloconApp.instance?.client?.databasePlugin?.register(
        FloconDatabaseModel(
            displayName = displayName,
            absolutePath = absolutePath,
        )
    )
}


interface FloconDatabasePlugin {
    fun register(floconDatabaseModel: FloconDatabaseModel)
}
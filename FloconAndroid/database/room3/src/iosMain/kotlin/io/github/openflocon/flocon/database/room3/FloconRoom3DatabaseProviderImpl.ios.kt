package io.github.openflocon.flocon.database.room3

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.database.core.databasePlugin
import io.github.openflocon.flocon.database.core.model.FloconDatabaseModel
import io.github.openflocon.flocon.dsl.FloconMarker

@OptIn(markerClass = [FloconMarker::class])
internal actual class FloconRoom3DatabaseProviderImpl actual constructor(
    private val context: FloconContext,
    paths: List<String>
) : FloconRoom3DatabaseProvider {

    actual override fun register() {
        val databases = getAllDataBases(emptyList())
        databases.forEach { Flocon.databasePlugin.register(it) }
    }

    @FloconMarker
    actual override fun getAllDataBases(registeredDatabases: List<FloconDatabaseModel>): List<FloconDatabaseModel> {
        return emptyList()
    }
}

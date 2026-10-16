package io.github.openflocon.flocon.database.room3

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.database.core.model.FloconDatabaseModel
import io.github.openflocon.flocon.dsl.FloconMarker

@OptIn(markerClass = [FloconMarker::class])
internal actual class FloconRoom3DatabaseProviderImpl actual constructor(
    private val context: FloconContext,
    paths: List<String>
) : FloconRoom3DatabaseProvider {

    actual override fun register() {
    }

    @FloconMarker
    actual override fun getAllDataBases(registeredDatabases: List<FloconDatabaseModel>): List<FloconDatabaseModel> {
        return emptyList()
    }
}

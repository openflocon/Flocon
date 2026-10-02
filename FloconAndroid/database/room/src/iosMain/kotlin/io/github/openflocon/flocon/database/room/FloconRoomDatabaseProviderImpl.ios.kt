package io.github.openflocon.flocon.database.room

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.database.core.model.FloconDatabaseModel
import io.github.openflocon.flocon.dsl.FloconMarker

internal actual class FloconRoomDatabaseProviderImpl actual constructor(
    private val context: FloconContext,
    paths: List<String>
) : FloconRoomDatabaseProvider {

    actual override fun register() {
        // no op on iOS
    }

    @OptIn(FloconMarker::class)
    actual override fun getAllDataBases(registeredDatabases: List<FloconDatabaseModel>): List<FloconDatabaseModel> {
        return emptyList()
    }
}

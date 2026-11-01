package io.github.openflocon.flocon.database.room3

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.database.core.model.FloconDatabaseModel

internal actual class FloconRoom3DatabaseProviderImpl actual constructor(
    private val context: FloconContext,
    paths: List<String>
) : FloconRoom3DatabaseProvider {

    actual override fun register() {
    }

    actual override fun getAllDataBases(registeredDatabases: List<FloconDatabaseModel>): List<FloconDatabaseModel> {
        return emptyList()
    }
}

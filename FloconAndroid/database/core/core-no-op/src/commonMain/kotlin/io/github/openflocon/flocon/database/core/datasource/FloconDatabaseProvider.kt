package io.github.openflocon.flocon.database.core.datasource

import io.github.openflocon.flocon.database.core.model.FloconDatabaseModel

interface FloconDatabaseProvider {

    fun getAllDataBases(
        registeredDatabases: List<FloconDatabaseModel>
    ): List<FloconDatabaseModel>

}

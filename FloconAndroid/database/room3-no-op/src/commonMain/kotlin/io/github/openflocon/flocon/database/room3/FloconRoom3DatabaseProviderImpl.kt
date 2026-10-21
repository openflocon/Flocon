package io.github.openflocon.flocon.database.room3

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.database.core.datasource.FloconDatabaseProvider
import io.github.openflocon.flocon.database.core.model.FloconDatabaseModel

interface FloconRoom3DatabaseProvider : FloconDatabaseProvider {
    fun register()
}

private object FloconRoom3DatabaseProviderNoOp : FloconRoom3DatabaseProvider {
    override fun register() = Unit
    override fun getAllDataBases(registeredDatabases: List<FloconDatabaseModel>): List<FloconDatabaseModel> = emptyList()
}

val Flocon.Companion.databaseRoom3: FloconRoom3DatabaseProvider
    get() = FloconRoom3DatabaseProviderNoOp

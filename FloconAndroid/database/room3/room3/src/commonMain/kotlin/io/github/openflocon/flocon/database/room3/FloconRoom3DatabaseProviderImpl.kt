package io.github.openflocon.flocon.database.room3

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.database.core.databasePlugin
import io.github.openflocon.flocon.database.core.datasource.FloconDatabaseProvider
import io.github.openflocon.flocon.database.core.model.FloconDatabaseModel

interface FloconRoom3DatabaseProvider : FloconDatabaseProvider {

    fun register()

}

internal expect class FloconRoom3DatabaseProviderImpl(
    context: FloconContext,
    paths: List<String>
) : FloconRoom3DatabaseProvider {
    override fun register()
    override fun getAllDataBases(registeredDatabases: List<FloconDatabaseModel>): List<FloconDatabaseModel>
}

val Flocon.Companion.databaseRoom3: FloconRoom3DatabaseProvider
    get() = databasePlugin.providers
        .firstNotNullOfOrNull { it as? FloconRoom3DatabaseProvider }
        ?: run {
            FloconLogger.log("Room3 database provider not initialized")
            NoOpRoom3DatabaseProvider
        }

private object NoOpRoom3DatabaseProvider : FloconRoom3DatabaseProvider {
    override fun register() = Unit
    override fun getAllDataBases(registeredDatabases: List<FloconDatabaseModel>): List<FloconDatabaseModel> = emptyList()
}
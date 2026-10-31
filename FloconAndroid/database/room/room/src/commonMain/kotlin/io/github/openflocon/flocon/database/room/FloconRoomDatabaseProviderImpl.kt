package io.github.openflocon.flocon.database.room

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.database.core.databasePlugin
import io.github.openflocon.flocon.database.core.datasource.FloconDatabaseProvider
import io.github.openflocon.flocon.database.core.model.FloconDatabaseModel

interface FloconRoomDatabaseProvider : FloconDatabaseProvider {

    fun register()

}

internal expect class FloconRoomDatabaseProviderImpl(
    context: FloconContext,
    paths: List<String>
) : FloconRoomDatabaseProvider {
    override fun register()
    override fun getAllDataBases(registeredDatabases: List<FloconDatabaseModel>): List<FloconDatabaseModel>
}

val Flocon.Companion.databaseRoom: FloconRoomDatabaseProvider
    get() = databasePlugin.providers
        .firstNotNullOfOrNull { it as? FloconRoomDatabaseProvider }
        ?: run {
            FloconLogger.log("Room database provider not initialized")
            NoOpRoomDatabaseProvider
        }

private object NoOpRoomDatabaseProvider : FloconRoomDatabaseProvider {
    override fun register() = Unit
    override fun getAllDataBases(registeredDatabases: List<FloconDatabaseModel>): List<FloconDatabaseModel> = emptyList()
}
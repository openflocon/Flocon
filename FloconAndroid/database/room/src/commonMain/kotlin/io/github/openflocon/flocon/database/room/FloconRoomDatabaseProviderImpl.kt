package io.github.openflocon.flocon.database.room

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.database.core.databasePlugin
import io.github.openflocon.flocon.database.core.datasource.FloconDatabaseProvider
import io.github.openflocon.flocon.database.core.model.FloconDatabaseModel
import io.github.openflocon.flocon.dsl.FloconMarker

interface FloconRoomDatabaseProvider : FloconDatabaseProvider {

    fun register()

}

@OptIn(FloconMarker::class)
internal expect class FloconRoomDatabaseProviderImpl(
    context: FloconContext,
    paths: List<String>
) : FloconRoomDatabaseProvider {
    override fun register()
    override fun getAllDataBases(registeredDatabases: List<FloconDatabaseModel>): List<FloconDatabaseModel>
}

@OptIn(FloconMarker::class)
val Flocon.Companion.databaseRoom: FloconRoomDatabaseProvider
    get() = databasePlugin.providers
        .firstNotNullOfOrNull { it as? FloconRoomDatabaseProvider }
        ?: error("Room database provider not initialized")
package io.github.openflocon.flocon.database.room

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.database.core.databasePlugin
import io.github.openflocon.flocon.database.core.datasource.FloconDatabaseProvider
import io.github.openflocon.flocon.database.core.model.FloconDatabaseModel
import io.github.openflocon.flocon.database.core.model.fromdevice.sql.DeviceDataBaseDataModel
import io.github.openflocon.flocon.dsl.FloconMarker

interface FloconRoomDatabaseProvider : FloconDatabaseProvider {

    // TODO
    fun register()

}

@OptIn(FloconMarker::class)
internal class FloconRoomDatabaseProviderImpl(
    paths: List<String>
) : FloconRoomDatabaseProvider {

    override fun getAllDataBases(
        registeredDatabases: List<FloconDatabaseModel>
    ): List<DeviceDataBaseDataModel> {
        TODO("Not yet implemented")
    }

    override fun register() {
        TODO("Not yet implemented")
    }

}

@OptIn(FloconMarker::class)
val Flocon.Companion.databaseRoom: FloconRoomDatabaseProvider
    get() = databasePlugin.providers
        .firstNotNullOfOrNull { it as? FloconRoomDatabaseProvider }
        ?: error("Room database provider not initialized")
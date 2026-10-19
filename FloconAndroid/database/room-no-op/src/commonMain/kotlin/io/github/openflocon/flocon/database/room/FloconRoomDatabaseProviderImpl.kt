package io.github.openflocon.flocon.database.room

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.database.core.datasource.FloconDatabaseProvider
import io.github.openflocon.flocon.database.core.model.FloconDatabaseModel
import io.github.openflocon.flocon.dsl.FloconMarker

interface FloconRoomDatabaseProvider : FloconDatabaseProvider {
    fun register()
}

@OptIn(FloconMarker::class)
private object FloconRoomDatabaseProviderNoOp : FloconRoomDatabaseProvider {
    override fun register() = Unit
    override fun getAllDataBases(registeredDatabases: List<FloconDatabaseModel>): List<FloconDatabaseModel> = emptyList()
}

val Flocon.Companion.databaseRoom: FloconRoomDatabaseProvider
    get() = FloconRoomDatabaseProviderNoOp

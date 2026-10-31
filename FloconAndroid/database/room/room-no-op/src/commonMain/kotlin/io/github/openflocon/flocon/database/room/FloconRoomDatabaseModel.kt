package io.github.openflocon.flocon.database.room

import androidx.room.RoomDatabase
import io.github.openflocon.flocon.database.core.FloconDatabasePlugin

fun FloconDatabasePlugin.register(id: String, displayName: String, database: RoomDatabase) {
    // no op
}

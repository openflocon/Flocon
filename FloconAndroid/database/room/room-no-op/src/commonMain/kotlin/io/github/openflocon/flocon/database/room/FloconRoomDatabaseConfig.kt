package io.github.openflocon.flocon.database.room

import io.github.openflocon.flocon.database.core.FloconDatabaseConfig

class FloconRoomDatabaseConfig internal constructor() {
    fun path(path: String) {
        // no op
    }
}

fun FloconDatabaseConfig.room(block: FloconRoomDatabaseConfig.() -> Unit = {}) {
    // no op
}

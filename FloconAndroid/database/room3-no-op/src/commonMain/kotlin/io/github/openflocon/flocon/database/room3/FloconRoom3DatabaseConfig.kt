package io.github.openflocon.flocon.database.room3

import io.github.openflocon.flocon.database.core.FloconDatabaseConfig

class FloconRoom3DatabaseConfig internal constructor() {
    fun path(path: String) {
        // no op
    }
}

fun FloconDatabaseConfig.room(block: FloconRoom3DatabaseConfig.() -> Unit = {}) {
    // no op
}

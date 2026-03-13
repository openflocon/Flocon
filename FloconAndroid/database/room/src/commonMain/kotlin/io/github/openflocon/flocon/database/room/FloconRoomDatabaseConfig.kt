package io.github.openflocon.flocon.database.room

import io.github.openflocon.flocon.database.core.FloconDatabaseConfig
import io.github.openflocon.flocon.dsl.FloconMarker

class FloconRoomDatabaseConfig internal constructor() {
    internal val paths: MutableList<String> = mutableListOf()

    fun path(path: String) {
        paths.add(path)
    }

}

@OptIn(FloconMarker::class)
fun FloconDatabaseConfig.room(block: FloconRoomDatabaseConfig.() -> Unit = {}) {
    val config = FloconRoomDatabaseConfig().apply(block)

    providers.add(
        FloconRoomDatabaseProviderImpl(
            context = context,
            paths = config.paths
        )
    )
}
package io.github.openflocon.flocon.database.room

import io.github.openflocon.flocon.database.core.FloconDatabaseConfig

class FloconRoomDatabaseConfig internal constructor() {
    internal val paths: MutableList<String> = mutableListOf()

    fun path(path: String) {
        paths.add(path)
    }

}

fun FloconDatabaseConfig.room(block: FloconRoomDatabaseConfig.() -> Unit = {}) {
    val config = FloconRoomDatabaseConfig().apply(block)

    providers.add(
        FloconRoomDatabaseProviderImpl(
            context = context,
            paths = config.paths
        )
    )
}
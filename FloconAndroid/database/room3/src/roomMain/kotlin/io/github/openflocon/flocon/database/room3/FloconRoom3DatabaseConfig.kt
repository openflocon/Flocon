package io.github.openflocon.flocon.database.room3

import io.github.openflocon.flocon.database.core.FloconDatabaseConfig

class FloconRoom3DatabaseConfig internal constructor() {
    internal val paths: MutableList<String> = mutableListOf()

    fun path(path: String) {
        paths.add(path)
    }

}

fun FloconDatabaseConfig.room(block: FloconRoom3DatabaseConfig.() -> Unit = {}) {
    val config = FloconRoom3DatabaseConfig().apply(block)

    providers.add(
        FloconRoom3DatabaseProviderImpl(
            context = context,
            paths = config.paths
        )
    )
}